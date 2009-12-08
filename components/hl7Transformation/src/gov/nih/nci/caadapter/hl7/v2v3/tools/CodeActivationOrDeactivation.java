package gov.nih.nci.caadapter.hl7.v2v3.tools;

import gov.nih.nci.caadapter.common.function.DateFunction;
import gov.nih.nci.caadapter.common.util.FileUtil;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Apr 1, 2009
 * Time: 11:51:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class CodeActivationOrDeactivation
{
    private String PROTECT_TAG_FILE_NAME = "cloned_caAdapter_by_umkis.txt";
    private String CLONED_CAADAPTER_DIR_NAME = "caAdapter_cloned_";

    private String CODE_TAG_SOURCE_DEACTIVATE = "//&umkis";
    private String CODE_TAG_TARGET_ACTIVATE =   "/*&umk*/";
    private String CODE_TAG_SOURCE_ACTIVATE =   "/*#umk*/";
    private String CODE_TAG_TARGET_DEACTIVATE = "//#umkis";

    private boolean schemaTag = false;
    CodeActivationOrDeactivation(String dirS)
    {
        doMain(dirS);
    }
    CodeActivationOrDeactivation(String dirS, boolean schemaTag)
    {
        this.schemaTag = schemaTag;
        doMain(dirS);
    }
    private void doMain(String dirS)
    {
        if ((dirS == null)||(dirS.trim().equals("")))
        {
            System.out.println("Directory is null");
            return;
        }
        dirS = dirS.trim();
        File dir = new File(dirS);
        if ((!dir.exists())||(!dir.isDirectory()))
        {
            System.out.println("Directory is not exist. : " + dirS);
            return;
        }

        String res = downloadFileFromURL("TestIPAddress.java");
        if ((res == null)||(res.trim().equals("")))
        {
            System.out.println("Stellar customization server is not working. : " + dirS);
            return;
        }

        String dirName = dir.getAbsolutePath();
        if (!dirName.endsWith(File.separator)) dirName = dirName + File.separator;

        File tagFile = new File(PROTECT_TAG_FILE_NAME);
        if ((tagFile.exists())&&(tagFile.isFile()))
        {
            System.out.println("Source caAdapter dir is already cloned. : ");
            return;
        }
        DateFunction df = new DateFunction();
        String currTime = df.getCurrentTime();
        dirName = dirName + CLONED_CAADAPTER_DIR_NAME + currTime.substring(0,12);
        File dirNew = new File(dirName);
        if ((dirNew.exists())&&(dirNew.isDirectory()))
        {
            System.out.println("This Directory is already exist. : " + dirName);
            return;
        }
        if (!dirNew.mkdir())
        {
            System.out.println("Directory creation Failure! : " + dirName);
            return;
        }
        dirName = dirName + File.separator;

        try
        {
            saveStringIntoFile(dirName + PROTECT_TAG_FILE_NAME, "Cloned on " + currTime + " from " + FileUtil.getWorkingDirPath());
        }
        catch(IOException ie)
        {
            System.out.println("Protect tag file writing error! : " + ie.getMessage());
            return;
        }

        try
        {
            cloneDirectory(new File(FileUtil.getWorkingDirPath()), dirNew);
            String metaInf = FileUtil.searchDir("META-INF", dirNew);
            if (metaInf != null)
            {
                if (!metaInf.endsWith(File.separator)) metaInf = metaInf + File.separator;
                File dirNew2 = new File(metaInf + "hl7_home");
                if (dirNew2.mkdir()) cloneDirectory(new File(FileUtil.searchDir("hl7_home")), dirNew2);
                File dirNew3 = new File(metaInf + "conf");
                if (dirNew3.mkdir()) cloneDirectory(new File(FileUtil.searchDir("conf")), dirNew3);
            }
        }
        catch(Exception ie)
        {
            System.out.println("Error! : " + ie.getMessage());
            return;
        }
    }

    private void cloneDirectory(File source, File target) throws IOException
    {
        if (source == null) throw new IOException("Source dir is null");
        if (target == null) throw new IOException("Target dir is null");
        if ((!source.exists())||(!source.isDirectory())) throw new IOException("Source dir is not exist.");
        if ((!target.exists())||(!target.isDirectory())) throw new IOException("Target dir is not exist.");

        File[] list = source.listFiles();
        String targetDirName = target.getAbsolutePath();
        if (!targetDirName.endsWith(File.separator)) targetDirName = targetDirName + File.separator;

        for (File file:list)
        {
            if (file.isFile())
            {
                copyFile(file, targetDirName);
                continue;
            }
            else if (!file.isDirectory()) continue;

            String dirName = file.getName();
            if (dirName.equalsIgnoreCase("cvs")) continue;
            if (dirName.equalsIgnoreCase("build")) continue;
            if (dirName.equalsIgnoreCase("dist")) continue;
            if (dirName.equalsIgnoreCase("dist2")) continue;
            if (dirName.equalsIgnoreCase("classes")) continue;
            if (dirName.equalsIgnoreCase("gencode")) continue;
            if (dirName.equalsIgnoreCase("log")) continue;
            //if ((simpleTag)&&(dirName.equalsIgnoreCase("docs"))) continue;
            //if ((simpleTag)&&(dirName.equalsIgnoreCase("demo"))) continue;

            File newDir = new File(targetDirName + dirName);
            if (!newDir.mkdir()) throw new IOException("Sub-Directory creation Failure! : " + targetDirName + dirName);

            cloneDirectory(file, newDir);
        }
    }

    private void copyFile(File file, String targetDirName) throws IOException
    {

        String fileName = file.getName();
        if (fileName.equalsIgnoreCase("CodeActivationOrDeactivation.java")) return;
        if ((fileName.toLowerCase().startsWith("caadapter"))&&(fileName.toLowerCase().indexOf("_src") > 0)&&(fileName.toLowerCase().endsWith(".zip"))) return;
        if ((fileName.toLowerCase().startsWith("caadapter"))&&(fileName.toLowerCase().indexOf("_bin") > 0)&&(fileName.toLowerCase().endsWith(".zip"))) return;

        boolean textTag = false;

        if (fileName.toLowerCase().endsWith(".java")) textTag = true;
        if (fileName.toLowerCase().endsWith(".txt")) textTag = true;
        if (fileName.toLowerCase().endsWith(".xml")) textTag = true;
        if (fileName.toLowerCase().endsWith(".iml")) textTag = true;
        if (fileName.toLowerCase().endsWith(".ipr")) textTag = true;
        if (fileName.toLowerCase().endsWith(".iws")) textTag = true;
        if (fileName.toLowerCase().endsWith(".properties")) textTag = true;
        if (fileName.toLowerCase().endsWith(".property")) textTag = true;
        if (fileName.toLowerCase().endsWith(".spp")) textTag = true;
        if (fileName.toLowerCase().endsWith(".bat")) textTag = true;
        if (fileName.toLowerCase().endsWith(".htm")) textTag = true;
        if (fileName.toLowerCase().endsWith(".html")) textTag = true;
        if (fileName.toLowerCase().endsWith(".xsd")) textTag = true;
        if (fileName.toLowerCase().endsWith(".fls")) textTag = true;
        if (fileName.toLowerCase().endsWith(".scs")) textTag = true;
        if (fileName.toLowerCase().endsWith(".h3s")) textTag = true;
        if (fileName.toLowerCase().endsWith(".map")) textTag = true;
        if (fileName.toLowerCase().endsWith(".xmi")) textTag = true;
        if (fileName.toLowerCase().endsWith(".vom")) textTag = true;
        if (fileName.toLowerCase().endsWith(".dtd")) textTag = true;
        if (fileName.toLowerCase().endsWith(".uml")) textTag = true;
        if (fileName.toLowerCase().endsWith(".mif")) textTag = true;
        if (fileName.toLowerCase().endsWith(".hmd")) textTag = true;
        if (fileName.toLowerCase().endsWith(".jsp")) textTag = true;
        if (fileName.toLowerCase().endsWith(".csv")) textTag = true;
        if (fileName.toLowerCase().endsWith(".js")) textTag = true;
        if (fileName.toLowerCase().endsWith(".policy")) textTag = true;
        if (fileName.toLowerCase().endsWith(".hl7")) textTag = true;




        boolean downloadTag = false;
        //if (fileName.equals("HSMNodePropertiesPane.java")) downloadTag = true;
        if (fileName.equals("MapProcessor.java")) downloadTag = true;
        if (fileName.equals("DatatypeProcessor.java")) downloadTag = true;
        if (fileName.equals("XMLElement.java")) downloadTag = true;
        if (fileName.equals("StringFunction.java")) downloadTag = true;
        if (fileName.equals("MapProcessorHelper.java")) downloadTag = true;
        //if (fileName.equals("caAdapterTransformationService.java")) downloadTag = true;
        if (fileName.equals("mif.zip")) downloadTag = true;
        if (fileName.equals("Attribute.java"))
        {
            if (targetDirName.indexOf("transformation") > 0) downloadTag = true;
        }
        if (fileName.equals("web.xml")) downloadTag = true;
        if (fileName.equals("AddNewScenario.java"))
        {
            List<String> list = new ArrayList<String>();
            String d = targetDirName + "stellar" + File.separator;
            File dD = new File(d);
            if ((!dD.exists())||(!dD.isDirectory()))
            {
                if (!dD.mkdirs())
                {
                    System.out.println("##### New Directory creation failure : " + d);
                    return;
                }
            }
            list.add("CaAdapterUserWorks.java");
            list.add("CaadapterWSUtil.java");
            list.add("DeleteOutputFile.java");
            list.add("DosFileHandler.java");
            list.add("FileUploaderWS.java");
            list.add("GeneralUtilitiesWS.java");
            list.add("ManageCaadapterWSUser.java");
            list.add("MultipartRequest.java");
            list.add("ScenarioFileRegistration.java");
            list.add("TestIPAddress.java");
            list.add("TransformationServiceOnWeb.java");
            list.add("MenuStart.java");
            list.add("TransformationServiceMain.java");
            list.add("TransformationServiceWithWSDL.java");


            for(String line:list)
            {
                File f = new File(d + line);
                copyFile(f, d);
            }
        }
        if ((!file.exists())||(!file.isFile()))
        {
            downloadTag = true;
        }

        if (!textTag)
        {
            if (downloadTag)
            {
                String tempFile = downloadFileFromURL(fileName);
                if ((tempFile != null)&&(!tempFile.trim().equals(""))) file = new File(tempFile);
                else System.out.println("##### Binary file download failure : " + fileName);
            }

            copyBinaryFile(file, targetDirName);
            //copyBinaryFileWithURI(file, targetDirName);
            return;
        }

        String oriFile = "";
        if (downloadTag)
        {
            String tempFile = downloadFileFromURL(fileName);

            if ((tempFile == null)||(tempFile.equals("")))
            {
                System.out.println("##### ERROR Text File Download failure : " + fileName);
                return;
            }
            else
            {
                System.out.println("-- Downloaded File : " + fileName);
                oriFile = tempFile;
            }
        }
        else oriFile = file.getAbsolutePath();

        System.out.println("Copy file (text)   : " + targetDirName + fileName);
        saveStringIntoFile(targetDirName + fileName, FileUtil.readFileIntoList(oriFile));
    }
    private String downloadFileFromURL(String fileName)
    {
        String[] urls = new String[] {"http://10.1.1.61:8080/file_exchange/",
                                      "http://155.230.210.233:8080/file_exchange/"};
        String tempFile = null;
        for(int i=0;i<urls.length;i++)
        {
            try
            {
                tempFile = FileUtil.downloadFromURLtoTempFile(urls[i] + fileName);
            }
            catch(IOException ie)
            {
                tempFile = null;
            }
            if ((tempFile != null)&&(!tempFile.trim().equals("")))
            {
                tempFile = tempFile.trim();
                break;
            }
        }
        return tempFile;
    }
    private void copyBinaryFileWithURI(File file, String targetDirName) throws IOException
    {
        String uri = file.toURI().toString();
        String url = file.toURI().toURL().toString();
        //System.out.println("FFF : " + file.getAbsolutePath() + " : " + uri + " : " + url);

        if (url.toLowerCase().startsWith("file:///")) {}
        else if (url.toLowerCase().startsWith("file://")) url = url.replace("file://", "file:///");
        else if (url.toLowerCase().startsWith("file:/")) url = url.replace("file:/", "file:///");
        System.out.println("Copy file (binary) : " + url);
        String name = FileUtil.downloadFromURLtoTempFile(url);
        File r = new File(name);
        r.renameTo(new File(targetDirName + file.getName()));
    }
    private void copyBinaryFile(File file, String targetDirName) throws IOException
    {

        String fileName = file.getName();
        System.out.print("Copy file (binary) : " + targetDirName + fileName);
        //DataInputStream distr = null;
        FileInputStream fis = null;

        FileOutputStream fos = null;
        //DataOutputStream dos2 = null;
        try
        {
            fis = new FileInputStream(file);
            //distr = new DataInputStream(fis);

            fos = new FileOutputStream(targetDirName + fileName);
            //dos2 = new DataOutputStream(fos);

            byte nn = 0;
            boolean endSig = false;
            byte[] bytes = new byte[fis.available()];
            int n = 0;
            while(true)
            {
                int q = -1;


                q = fis.read(bytes);

                if (q != bytes.length)
                {
                    System.out.println(" : ERROR => Different length : " + q + " : " + bytes.length);
                    break;
                }
                else System.out.println(" : *** GOOD => File length : " + q);
                fos.write(bytes);
                break;
            }
        }
        catch(IOException cse)
        {
            throw new IOException("Binary File Input failure... (IOException) " + cse.getMessage());
        }
        catch(Exception ne)
        {
            throw new IOException("Binary File Input failure... (Excep) " + ne.getMessage());
        }
        finally
        {
            if (fis != null) fis.close();
            //if (distr != null) distr.close();
            if (fos != null) fos.close();
            //if (dos2 != null) dos2.close();
        }
        if ((schemaTag)&&(fileName.equalsIgnoreCase("schemas.zip"))) extractSchemaZipFile(file, targetDirName);
    }
    private void extractSchemaZipFile(File file, String targetDirName) throws IOException
    {
        File f = new File(targetDirName + "schemas");
        if (!f.mkdir()) throw new IOException("Schema directory creation failure : " + targetDirName);

        String schemaDir = targetDirName + "schemas" + File.separator;

        ZipFile xsdZipFile=new ZipFile(file);
        if (xsdZipFile == null) throw new IOException("Schema zip file object creation failure : " + targetDirName);

        Enumeration<? extends ZipEntry> enumer = xsdZipFile.entries();

        while(enumer.hasMoreElements())
        {
            ZipEntry entry = enumer.nextElement();
            String entryName = entry.getName();
            if (!File.separator.equals("/"))
            {
                entryName = entryName.replace("/", File.separator);
            }
            String name = schemaDir + entryName;
            if (entry.isDirectory())
            {
                File fl = new File(name);
                if (!fl.mkdirs()) throw new IOException("Schema zip entry directory creation failure : " + schemaDir + entryName);
                else System.out.println("    Success sub directory creation for Schema zip entry 1 : " + schemaDir + entryName);

                continue;
            }

            int idx = name.lastIndexOf(File.separator);
            String par = name.substring(0, idx);

            File fl = new File(par);
            if ((!fl.exists())||(!fl.isDirectory()))
            {
                if (!fl.mkdirs()) throw new IOException("Schema sub directory creation failure : " + par);
                else System.out.println("    Success sub directory creation for Schema zip entry 2 : " + par);
            }


            FileOutputStream fos = null;
            //DataOutputStream dos2 = null;
            int cnt =0;
            try
            {
                //byte[] bytes = new byte[(int)entry.getSize()];
                InputStream stream = xsdZipFile.getInputStream(entry);
                //byte[] bytes = new byte[stream.available()];
                //cnt = stream.read(bytes);
                //if (cnt != bytes.length) throw new IOException("Unmatched entry size ("+cnt+" : "+bytes.length+") : " + name);
                fos = new FileOutputStream(name);
                while(true)
                {
                    int bt = 0;
                    try
                    {
                        bt = stream.read();
                    }
                    catch(java.io.EOFException ee)
                    {
                        break;
                    }
                    if (bt < 0) break;
                    fos.write(bt);
                }
            }
            catch(IOException cse)
            {
                throw new IOException("Schema zip entry write failure... (IOException) " + cse.getMessage());
            }
            catch(Exception ne)
            {
                ne.printStackTrace();
                throw new IOException("Schema zip entry write failure... (Excep) " + ne.getMessage());
            }
            finally
            {
                if (fos != null) fos.close();
            }
            System.out.println("    Success writing Schema zip entry : " + name);

        }

    }
    private void copyBinaryFileWithDataStream(File file, String targetDirName) throws IOException
    {

        String fileName = file.getName();
        System.out.println("Copy file (binary) : " + targetDirName + fileName);
        DataInputStream distr = null;
        FileInputStream fis = null;

        FileOutputStream fos = null;
        DataOutputStream dos2 = null;
        try
        {
            fis = new FileInputStream(file);
            distr = new DataInputStream(fis);

            fos = new FileOutputStream(targetDirName + fileName);
            dos2 = new DataOutputStream(fos);

            byte nn = 0;
            boolean endSig = false;
            byte[] bytes = new byte[fis.available()];
            int n = 0;
            while(true)
            {
                int q = -1;
                for(int i=0;i<bytes.length;i++)
                {
                    try
                    {

                        bytes[i] = distr.readByte();
                    }
                    catch(EOFException ie)
                    {
                        endSig = true;
                    }

                    if (endSig) break;
                    q = i;
                }
                if (q == (bytes.length - 1)) dos2.write(bytes);
                else for(int i=0;i<q;i++) dos2.writeByte(bytes[i]);

                //dos2.writeByte(nn);
                if (endSig) break;
            }
        }
        catch(IOException cse)
        {
            throw new IOException("Binary File Input failure... (IOException) " + cse.getMessage());
        }
        catch(Exception ne)
        {
            throw new IOException("Binary File Input failure... (Excep) " + ne.getMessage());
        }
        finally
        {
            if (fis != null) fis.close();
            if (distr != null) distr.close();
            if (fos != null) fos.close();
            if (dos2 != null) dos2.close();
        }
    }
    private void saveStringIntoFile(String fileName, String string) throws IOException
    {
        List<String> list = new ArrayList<String>();
        list.add(string);
        saveStringIntoFile(fileName, list);
    }
    private void saveStringIntoFile(String fileName, List<String> string) throws IOException
    {
        FileWriter fw = null;

        try
        {
            fw = new FileWriter(fileName);
            for (int i=0;i<string.size();i++)
            {
                String line = string.get(i);
                if (fileName.toLowerCase().endsWith(".java"))
                {
                    line = replaceDeactivateTag(line, CODE_TAG_SOURCE_DEACTIVATE);
                    line = replaceActivateTag(line, CODE_TAG_SOURCE_ACTIVATE, CODE_TAG_TARGET_DEACTIVATE);
                }
                //if (fileName.equals("run.bat"))
                if (fileName.toLowerCase().endsWith("run.bat"))
                {
                    if (line.toLowerCase().startsWith("java ")) line = "java -Xmx150000000" + line.substring(4);
                }
                //if (fileName.equals("build.properties"))
                if (fileName.toLowerCase().endsWith("build.properties"))
                    line = modifyBuildProperties(line);
                fw.write(line + "\r\n");
            }
        }
        catch(Exception ie)
        {
            throw new IOException("File Writing Error(" + fileName + ") : " + ie.getMessage() + ", value : " + string);
        }
        finally
        {
            if (fw != null) fw.close();
        }
    }

    private String modifyBuildProperties(String line)
    {
        String ln = line.trim();

        if (ln.startsWith("project."))
        {
            if ((ln.indexOf("MTS") > 0)||(ln.indexOf("HL7") > 0)) {}
            else
            {
                if (ln.startsWith("project.release.version")) line = "project.release.version=caadapter_HL7";
                if (ln.startsWith("project.docs.home")) line = "project.docs.home=docs/4.3";
                if (ln.startsWith("project.user.guide")) line = "project.user.guide=caAdapter_HL7_MTS_v4.3_UsersGuide";
                if (ln.startsWith("project.installation.guide")) line = "project.installation.guide=caAdapter HL7 MTS v4.3 Installation Guide.pdf";
                if (ln.startsWith("proejct.online.help")) line = "proejct.online.help=help_4.3.zip";
            }
        }
        if (ln.startsWith("caadapter.release.mms.only")) line = "caadapter.release.mms.only=false";
        if (ln.startsWith("caadapter.release.mms.gme.only")) line = "caadapter.release.mms.gme.only=false";
        if (ln.startsWith("caadapter.release.hl7.only")) line = "caadapter.release.hl7.only=true";
        if (ln.startsWith("caadapter.release.ws.include")) line = "caadapter.release.ws.include=true";
        if (ln.startsWith("caadapter.release.all.modules")) line = "caadapter.release.all.modules=false";

        //System.out.println("XXXX : " + line);
        return line;
    }

    private String replaceActivateTag(String line, String from, String to)
    {
        int idx = line.toLowerCase().indexOf(from);

        if (idx < 0) return line;
        else if (idx == 0) return to + line.substring(from.length());

        return line.substring(0, idx) + to + line.substring(idx + from.length());
    }
    private String replaceDeactivateTag(String line, String from)
    {
        String line2 = line.trim();
        int idx = line2.toLowerCase().indexOf(from);

        if (idx < 0) return line;
        else if (idx == 0)
        {
            String tTag = CODE_TAG_SOURCE_DEACTIVATE + ":INSERT=";
            if (line2.startsWith(tTag))
            {
                String res = downloadFileFromURL(line2.substring(tTag.length()));
                if ((res != null)&&(!res.trim().equals("")))
                {
                    String rr = FileUtil.readFileIntoString(res);
                    if ((rr != null)&&(!rr.trim().equals(""))) return rr;
                }
            }
            else return line2.substring(from.length()) + "     " +from;
        }

        return line;
    }

    public static void main(String[] arg)
    {
        new CodeActivationOrDeactivation("c:\\clone_caAdapter");
        //new CodeActivationOrDeactivation("c:\\clone_caAdapter", true);
    }

}
