package ru.heroicrealm.scormbuilder.service.impl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.heroicrealm.scormbuilder.entities.*;
import ru.heroicrealm.scormbuilder.service.*;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by kuran on 03.02.2019.
 */
@MessageDriven(mappedName = "scormPacker", activationConfig = {
        @ActivationConfigProperty(propertyName = "messagingType", propertyValue = "javax.jms.MessageListener"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/ScormPackerQueue"),
        @ActivationConfigProperty(propertyName = "connectionFactoryName", propertyValue = "ConnectionFactory"),
        @ActivationConfigProperty(propertyName = "maxPoolSize", propertyValue = "10"),
        @ActivationConfigProperty(propertyName = "maxMessages", propertyValue = "10"),
        @ActivationConfigProperty(propertyName = "useJNDI", propertyValue = "true")
})
public class ScormPacker implements MessageListener {

    public static final String URI_LOM = "http://ltsc.ieee.org/xsd/LOM";
    public static final String URI_ADLCP = "http://www.adlnet.org/xsd/adlcp_v1p3";
    public static final String URI_XMLNS = "http://www.w3.org/2000/xmlns/";
    public static final String URI_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    @EJB
    ICatalogService catalogService;
    @EJB
    IPackageService packageService;
    @EJB
    IPresentationService presentationService;
    @EJB
    ITaskService taskService;
    @EJB
    IConfigService configService;

    DocumentBuilderFactory docFactory;
    DocumentBuilder docBuilder;

    public ScormPacker() throws ParserConfigurationException {
        docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        docBuilder = docFactory.newDocumentBuilder();
    }


    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Set<Long> packages = (Set<Long>) objectMessage.getObject();
            Task task = taskService.createTask(Task.TaskType.PACKAGE, "Task1", "USER");
            String storagePath = configService.getProperty(IConfigService.FS_BASE_PATH);
            File outf = new File(storagePath+File.separator+"out");
            if(!outf.exists()) {
                outf.mkdirs();
            }
            new File(storagePath+File.separator + task.getGuid()).mkdir();
            for (Long packId : packages) {
                processPackage(packId, storagePath,task);
            }
            File f = new File(storagePath+File.separator+task.getGuid());
            File[] files = f.listFiles();

            FileOutputStream fos = new FileOutputStream(outf+File.separator+task.getGuid()+".zip");
            ZipOutputStream zos = new ZipOutputStream(fos);
            addDir(new File(storagePath+File.separator+task.getGuid()),zos,storagePath+File.separator);
            zos.close();
            fos.close();
            Arrays.stream(files).forEach(file -> file.delete());
            f.delete();
            taskService.finishTask(task);



        } catch (JMSException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void processPackage(Long packId, String storagePath, Task task) throws Exception {
        String basePath = storagePath + File.separator + task.getGuid() + File.separator + packId;
        new File(basePath).mkdir();

        ScormPackage pack = packageService.load(packId);
        CatalogEntry ce = catalogService.load(packId);

        List<ScormRef> components = pack.getComponents();

        LinkedList<String> manifest = new LinkedList<>();
        loadManifestTemplate(storagePath, manifest);
        manifest.set(0,"<?xml version=\"1.0\"?>");
        manifest.set(11,"                    <lom:string>"+ce.getName()+"</lom:string>");
        manifest.set(27,"            <title>"+ce.getName()+"</title>");
        LinkedList<String> items = new LinkedList<>();
        LinkedList<String> resources = new LinkedList<>();
        for (ScormRef ref : components) {
            String componentPath = basePath + File.separator+"res" +File.separator+ ref.getTargetId();
            new File(componentPath).mkdirs();
            exportPesentation(storagePath,ref.getSeqnr(),ref.getTargetId(), componentPath, task,items,resources);
        }
        manifest.addAll(items);
        manifest.add("            <imsss:sequencing>");
        manifest.add("                <imsss:controlMode flow=\"true\"/>");
        manifest.add("            </imsss:sequencing>");
        manifest.add("        </organization>");
        manifest.add("    </organizations>");
        manifest.add("    <resources>");
        manifest.addAll(resources);
        manifest.add("    </resources>");
        manifest.add("</manifest>");
        String manifestFileName = basePath + File.separator+"imsmanifest.xml";
        Writer bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(manifestFileName), "UTF-8"));
        manifest.stream().forEach(s -> {
            try {
                bw.write(s);
                bw.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bw.close();
        zipPackage(storagePath, ce,pack,task);
        System.out.println("Deleting:"+basePath);
        deleteDirectoryRecursion(new File(basePath).toPath());

    }
    void deleteDirectoryRecursion(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        Files.delete(path);
    }
    private void zipPackage(String storagePath,  CatalogEntry ce, ScormPackage pack, Task task) throws IOException {
        String basePath = storagePath + File.separator + task.getGuid() + File.separator + pack.getId();
        ZipOutputStream zos  =new ZipOutputStream(
                new FileOutputStream(storagePath+File.separator+task.getGuid()+File.separator+pack.getId()+"_"+ce.getName()+".zip"));
        ZipEntry manifest = new ZipEntry("imsmanifest.xml");
        zos.putNextEntry(manifest);
        writeToZip(zos,basePath+File.separator+"imsmanifest.xml");
        zos.closeEntry();
        File res = new File(basePath+File.separator+"res");
        addDir(res,zos,basePath+File.separator);
        zos.close();
    }

    private void writeToZip(ZipOutputStream zos, String file) throws IOException {
        byte buffer[] = new byte[4096];
        FileInputStream fis = new FileInputStream(file);
        int len = fis.read(buffer);
        while (len != -1) {
            zos.write(buffer, 0, len);
            len = fis.read(buffer);
        }
        fis.close();
    }

    private void loadManifestTemplate(String storagePath, LinkedList<String> manifest) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(storagePath+File.separator+"template"+File.separator+"imsmanifest.xml"));
        String s;
        while ((s=br.readLine())!=null){
            manifest.add(s);
        }
        br.close();
    }


    private void exportPesentation(String storagePath,int seqnr, long presentationId, String basePath, Task task, LinkedList<String> items, LinkedList<String> resources) throws IOException {
        File player = new File( storagePath+File.separator+"template"+File.separator+"player.html");
        File dsplayer = new File(basePath + File.separator+"player.html");
        Files.copy(player.toPath(), dsplayer.toPath(), StandardCopyOption.REPLACE_EXISTING);
        File jsData = new File(basePath +File.separator +"pages.js");
        //BufferedWriter bw = new BufferedWriter(new FileWriter(jsData));
        Writer bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(basePath + File.separator+"pages.js"), "UTF-8"));
        bw.write("var pages = [");
        Presentation p = presentationService.load(presentationId);

        for (int i = 0; i < p.getPages().size(); i++) {

            String s = p.getPages().get(i).getContent().replaceAll("\"", "\\\\\"").replaceAll("\\r\\n|\\r|\\n", " ");
            bw.write("\"");
            bw.write(s);
            bw.write("\"");
            if (i < p.getPages().size() - 1) {
                bw.write(",");
            }

        }
        bw.write("];");
        bw.close();

        items.add("            <item identifier=\"item"+seqnr+"\" identifierref=\"resource"+seqnr+"\">");
        items.add("                 <title>"+p.getTitle()+"</title>");
        items.add("            </item>");
        resources.add("        <resource identifier=\"resource"+seqnr+"\" type=\"webcontent\" adlcp:scormType=\"sco\" href=\"res/"+presentationId+"/player.html\">");
        resources.add("             <file href=\"res/"+presentationId+"/player.html\"/>");
        resources.add("             <file href=\"res/"+presentationId+"/pages.js\"/>");
        resources.add("        </resource>");
    }



     void addDir(File dirObj, ZipOutputStream out,String prefix) throws IOException {
        File[] files = dirObj.listFiles();
        byte[] tmpBuf = new byte[1024];

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                addDir(files[i], out,prefix);
                continue;
            }
            FileInputStream in = new FileInputStream(files[i].getAbsolutePath());

            String substring = files[i].getAbsolutePath().substring(prefix.length());
            System.out.println(" Adding: " + files[i].getAbsolutePath()+":"+substring);
            out.putNextEntry(new ZipEntry(substring));
            int len;
            while ((len = in.read(tmpBuf)) > 0) {
                out.write(tmpBuf, 0, len);
            }
            out.closeEntry();
            in.close();
        }
    }
}
