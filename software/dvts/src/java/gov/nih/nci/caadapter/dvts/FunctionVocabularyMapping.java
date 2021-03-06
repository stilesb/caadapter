/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts;

import gov.nih.nci.caadapter.dvts.common.function.FunctionException;
import gov.nih.nci.caadapter.dvts.common.function.FunctionUtil;
import gov.nih.nci.caadapter.dvts.common.function.FunctionVocabularyXMLMappingEventHandler;
import gov.nih.nci.caadapter.dvts.common.function.FunctionVocabularyMappingEventHandler;
import gov.nih.nci.caadapter.dvts.common.ApplicationException;
import gov.nih.nci.caadapter.dvts.common.Message;
import gov.nih.nci.caadapter.dvts.common.meta.VocabularyMappingData;
import gov.nih.nci.caadapter.dvts.common.meta.ReturnMessage;
import gov.nih.nci.caadapter.dvts.common.meta.ErrorLevel;
import gov.nih.nci.caadapter.dvts.common.util.FileSearchUtil;
import gov.nih.nci.caadapter.dvts.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.dvts.common.util.FileUtil;
import gov.nih.nci.caadapter.dvts.common.util.ClassLoaderUtil;
import gov.nih.nci.caadapter.dvts.common.util.Config;
//import gov.nih.nci.caadapter.dvts.common.validation.XMLValidator;
import gov.nih.nci.caadapter.dvts.common.validation.xml.XMLValidator;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.stream.XMLStreamReader;
import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.net.*;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 13, 2011
 * Time: 4:42:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class FunctionVocabularyMapping
{

    /**
     * Logging VocabularyMapping used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: FunctionVocabularyMapping.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/map/FunctionVocabularyMapping.java,v 1.10 2009-08-20 00:24:59 altturbo Exp $";

    //private String domain = "";
    private String[] typeNamePossibleList = {"VOM_File_Local", "URL", "VOM_File_URL"};
    private String[] methodNamePossibleList = {"translateValue", "translateInverseValue"};
    private String type = typeNamePossibleList[0];
    private String value;
    private String tagForXMLFormatVOMFile = "%%XMLFormatVOMFile%%";
    //private String filePathOfVOMxsd = FileUtil.getWorkingDirPath() + "\\map\\functions\\vom.xsd";
    private String defaultDomainName = "defaultDomain";
    private String pathNameJustBeforeValidated = "";
    private boolean inverseTag = false;
    private File defaultWorkDirectory = null;
    private List<String[]> domainList = null;

    private FunctionVocabularyXMLMappingEventHandler recentVOMHandler = null;
    private String recentVOMFileName = null;
    //private FunctionVocabularyMappingEventHandler recentURL_VOMHandler = null;
    private VocabularyMappingData recentVocabularyMappingDataObj = null;

    private boolean elsecaseApplied = false;

    public FunctionVocabularyMapping()
    {
    }
    /*
    public FunctionVocabularyMapping(String fileName) throws FunctionException
    {
        setValue(fileName);
    }
    public FunctionVocabularyMapping(String typ, String fileName) throws FunctionException
    {
        setType(typ);
        setValue(fileName);
    }
    */
    public FunctionVocabularyMapping(String typ, String fileName, boolean inverse) throws FunctionException
    {
        setup(typ, fileName, inverse);
    }
    public FunctionVocabularyMapping(String typ, String fileName, String domainName, boolean inverse) throws FunctionException
    {
        if ((domainName != null)&&(!domainName.trim().equals("")))
            setup(typ, fileName+ Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR+domainName.trim(), inverse);
        else setup(typ, fileName, inverse);
    }
    public FunctionVocabularyMapping(String typ, String fileName, boolean inverse, File workDir) throws FunctionException
    {
        setDefaultWorkDirectory(workDir);
        setup(typ, fileName, inverse);
    }

    private void setup(String typ, String fileName, boolean inverse) throws FunctionException
    {
        if ((inverse)&&(typ.trim().equals(typeNamePossibleList[1])))
            throw new FunctionException("URL cannot be applied in case of inverse mapping : " + type, 720, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        inverseTag = inverse;
        setType(typ);
        setValue(fileName);
    }

    public String translateValue(String input) throws FunctionException
    {
        if (inverseTag)
        {
            throw new FunctionException("Mis-assigning forward mapping : ", 724, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
            //return translateInverseValue(input);
        }

        if (type.equals(typeNamePossibleList[0]))
        {
            String mapData = checkMappingFileAndExtractData(value);
            if (mapData.equals(tagForXMLFormatVOMFile))
            {
                return searchXMLMappingFile(input, false, value);
            }
            else return searchMappingFile(input, false, mapData);
        }
        else if (type.equals(typeNamePossibleList[1]))
        {
            return searchMappingURL(input).get(0);
        }
        else if (type.equals(typeNamePossibleList[2]))
        {
            return searchXMLMappingFile(input, false, value);
        }
        else throw new FunctionException("This is not a valid Vocabulary mapping type(forward) : " + type, 710, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

    }
    public String translateInverseValue(String input) throws FunctionException
    {
        if (!inverseTag)
        {
            throw new FunctionException("Mis-assigning inverse mapping : ", 722, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        }

        if (type.equals(typeNamePossibleList[0]))
        {
            String mapData = checkMappingFileAndExtractData(value);
            if (mapData.equals(tagForXMLFormatVOMFile))
            {
                return searchXMLMappingFile(input, true, value);
            }
            else return searchMappingFile(input, true, mapData);
        }
        else if (type.equals(typeNamePossibleList[2]))
        {
            return searchXMLMappingFile(input, true, value);
        }
        else throw new FunctionException("This is not a valid Vocabulary mapping type(inverse) : " + type, 711, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

    }

    public String[] extractDomainAndFileName(String fileNameSrc) throws FunctionException
    {
        String domain = "";
        String fileName = "";
        if (fileNameSrc.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) fileNameSrc = fileNameSrc.replace(Config.CAADAPTER_HOME_DIR_TAG, FileUtil.getWorkingDirPath());

        int idx = fileNameSrc.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR);
        if (idx > 0)
        {
            fileName = fileNameSrc.substring(0, idx);
            domain = fileNameSrc.substring(idx + Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR.length());
        }
        else fileName = fileNameSrc;
        String[] arrayRes = {domain, fileName};
        return arrayRes;
    }
    private String checkMappingFileAndExtractData(String fileNameSrc) throws FunctionException
    {
        String[] arrayRes = extractDomainAndFileName(fileNameSrc);
        String domain = arrayRes[0];
        String fileName = arrayRes[1];

        fileName = verifyFileName(fileName);

        File file = new File(fileName);

        if (type.equals(typeNamePossibleList[2]))
        {
            return getDomains(fileName).get(0);
        }

        if (file.exists())
        {
            if (file.isFile()) {}
            else throw new FunctionException("This vocabulary mapping path is not a file. : " + fileName, 702, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        else throw new FunctionException("Not exist vocabulary mapping file : " + fileName, 701, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        //String fileName = value;
        //if (fileName.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) fileName = fileName.replace(Config.CAADAPTER_HOME_DIR_TAG, FileUtil.getWorkingDirPath());
        String readLineOfFile = "";
        String content = "";
        boolean domainTag = false;
        boolean findTag = false;
        if (domain.equals(""))
        {
            domainTag = true;
            findTag = true;
        }
        try
        {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            int n = 0;
            while((readLineOfFile=br.readLine())!=null)
            {
                readLineOfFile = readLineOfFile.trim();

                if (readLineOfFile.equals("")) continue;

                if ((n==0)&&(readLineOfFile.startsWith("<")))
                {
                    fr.close();
                    br.close();
                    return tagForXMLFormatVOMFile;
                }
                n++;
                if (readLineOfFile.startsWith("#")) continue;

                boolean inverseAllowed = true;
                String startDomain = "&StartDomain:";
                String endDomain = "&EndDomain";
                if (readLineOfFile.toUpperCase().startsWith(startDomain.toUpperCase()))
                {
                    if (domain.equals("")) throw new FunctionException("Domain name is not given. : " + value + " : "+fileNameSrc, 708, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
                    readLineOfFile = readLineOfFile.substring(startDomain.length()).trim();

                    String inverseNotAllowed = "&InverseNotAllowed:";
                    if (readLineOfFile.toUpperCase().startsWith(inverseNotAllowed.toUpperCase()))
                    {
                        inverseAllowed = false;
                        readLineOfFile = readLineOfFile.substring(inverseNotAllowed.length()).trim();
                    }

                    String domainName = "";
                    String achar = "";
                    for (int i=0;i<readLineOfFile.length();i++)
                    {
                        achar = readLineOfFile.substring(i, i+1);
                        if ((achar.equals(" "))||(achar.equals(";"))||(achar.equals("\t"))) break;
                        domainName = domainName + achar;
                    }

                    if (domainName.equalsIgnoreCase(domain))
                    {
                        findTag = true;
                        domainTag = true;
                        readLineOfFile = readLineOfFile.substring(domainName.length()).trim();
                        if (readLineOfFile.startsWith(";")) readLineOfFile = readLineOfFile.substring(1).trim();
                        if (readLineOfFile.equals("")) continue;
                    }
                }
                if (readLineOfFile.toUpperCase().startsWith(endDomain.toUpperCase()))
                {
                    domainTag = false;
                    if ((!domain.equals(""))&&(findTag)) break;
                }
                if (domainTag)
                {
                    if (!readLineOfFile.equals("")) content = content + readLineOfFile + "\t";
                }
            }
            fr.close();
            br.close();
        }
        catch(IOException e)
        {
            throw new FunctionException("Vocabulary mapping file reading error. : " + value, 703, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        if (!findTag) throw new FunctionException("This domain name is not found. : " + domain, 709, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        return content;
    }

    private String searchMappingFile(String searchStr, boolean searchInverse, String content) throws FunctionException
    {
        if (content == null) content = "";
        if (searchStr == null) searchStr = "";
        if (content.trim().equals("")) throw new FunctionException("Content is Empty.", 786, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        if (searchStr.trim().equals("")) throw new FunctionException("Search word is empty.", 787, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        elsecaseApplied = false;
        //String content = mapData;
        String readLineOfFile = "";

        String s1 = "";
        String s2 = "";
        String st = "";
        boolean ignoreTag = false;
        String cont = "";
        for (int i=0;i<content.length();i++)
        {
            s1 = content.substring(i, i+1);
            if (s1.equals(";")) s1 = "\t";
            st = s2 + s1;
            if (st.equals("/*")) ignoreTag = true;
            if (st.equals("*/"))
            {
                ignoreTag = false;
                s1 = "";
                s2 = "";
            }
            if (!ignoreTag) cont = cont + s2;

            s2 = s1;
        }

        int idx1 = 0;
        StringTokenizer strTok = new StringTokenizer(cont, "\t");
        String elseCasesTag = "&elsecase";
        String elseInverseCasesTag = "&inverseelsecase";
        String caseKeepOriginalValue = "keepvalue";
        String caseDoubleQuotation = "doublequotation";
        String caseSingleQuotation = "singlequotation";
        String caseSpace = "space";
        String caseZero = "zero";
        String caseNull = "null";
        String caseValueAssign = "valueassign";
        String caseAssignValue = "assignvalue";
        String caseErrorMessage = "errormessage";
        String caseMakeAnError = "makeanerror";
        String caseTaggingSuffix = "taggingsuffix";
        String caseTaggingPrefix = "taggingprefix";
        String elseCasesWhatToDo = "";
        String assignedValue = "";
        String elseTag = "";
        if (searchInverse) elseTag = elseInverseCasesTag;
        else elseTag = elseCasesTag;
        while(strTok.hasMoreTokens())
        {
            readLineOfFile = (strTok.nextToken()).trim();
            if ((searchInverse)&&(readLineOfFile.toLowerCase().startsWith(elseCasesTag))) continue;
            if ((!searchInverse)&&(readLineOfFile.toLowerCase().startsWith(elseInverseCasesTag))) continue;
            if (readLineOfFile.toLowerCase().startsWith(elseTag))
            {
                elseCasesWhatToDo = readLineOfFile.substring(elseTag.length()).trim();
                String temp = "";
                boolean colonTag = false;
                for(int i=0;i<elseCasesWhatToDo.length();i++)
                {
                    String achar = elseCasesWhatToDo.substring(i, i+1);
                    if (achar.equals(":"))
                    {
                        colonTag = true;
                        continue;
                    }
                    if (!colonTag) continue;
                    if (!achar.equals(" ")) temp = temp + achar;
                    if ((temp.toLowerCase().equals(caseValueAssign))||
                        (temp.toLowerCase().equals(caseAssignValue))||
                        (temp.toLowerCase().equals(caseTaggingSuffix))||
                        (temp.toLowerCase().equals(caseTaggingPrefix)))
                    {
                        String temp2 = elseCasesWhatToDo.substring(i+1).trim();
                        if (temp2.startsWith("=")) temp2 = temp2.substring(1).trim();
                        else throw new FunctionException("Invalid Systax : '=' character has to be the next of "+temp2 + "' tag.", 797, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);

                        if ((temp2.startsWith("'"))||(temp2.startsWith("\""))) temp2 = temp2.substring(1);
                        if ((temp2.endsWith("'"))||(temp2.endsWith("\""))) temp2 = temp2.substring(0,temp2.length()-1);
                        assignedValue = temp2;
                        if (assignedValue.equals("")) throw new FunctionException("'"+temp + "' tag has to have its own value : ", 798, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);
                        break;
                    }
                }
                if (!colonTag) throw new FunctionException("Invalid ElseCase or InverseElseCase syntax, colon ':' character must be follow.", 788, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

                elseCasesWhatToDo = temp.toLowerCase();
                continue;
            }
            idx1 = readLineOfFile.indexOf(Config.VOCABULARY_MAP_FILE_VALUE_SEPARATOR);

            if (idx1 <= 0) continue;

            String src = "";
            String tgt = "";

            if(!searchInverse)
            {
                src = readLineOfFile.substring(0, idx1).trim();
                tgt = readLineOfFile.substring(idx1 + 2).trim();
            }
            else
            {
                tgt = readLineOfFile.substring(0, idx1).trim();
                src = readLineOfFile.substring(idx1 + 2).trim();
                //System.out.println("Invers case : search : " + searchStr + ", src : " + src + ", tgt : " + tgt);
                //if (src.equals(searchStr)) System.out.println("BINGO!!! ");
            }
            if (src.equals(searchStr))
            {
                elsecaseApplied = false;
                return tgt;
            }
        }
        String inverse;
        if (searchInverse) inverse = "inverse";
        else inverse = "forward";
        String errMsg = "Vocabulary mapping (" + inverse + ") search failure. Not found '" + searchStr + "' in " + value + " file";
        elsecaseApplied = true;
        if (elseCasesWhatToDo.equals(caseKeepOriginalValue)) return searchStr;
        else if (elseCasesWhatToDo.equals(caseKeepOriginalValue + "s")) return searchStr;
        else if (elseCasesWhatToDo.equals(caseDoubleQuotation)) return "\"\"";
        else if (elseCasesWhatToDo.equals(caseSingleQuotation)) return "''";
        else if (elseCasesWhatToDo.equals(caseSpace)) return " ";
        else if (elseCasesWhatToDo.equals(caseZero)) return "0";
        else if (elseCasesWhatToDo.equals(caseNull)) return "";
        else if (elseCasesWhatToDo.equals(caseValueAssign)) return assignedValue;
        else if (elseCasesWhatToDo.equals(caseAssignValue)) return assignedValue;
        else if (elseCasesWhatToDo.equals(caseErrorMessage)) return errMsg;
        else if (elseCasesWhatToDo.equals(caseTaggingSuffix)) return searchStr + assignedValue;
        else if (elseCasesWhatToDo.equals(caseTaggingPrefix)) return assignedValue + searchStr;
        else if (elseCasesWhatToDo.equals(caseMakeAnError)) throw new FunctionException(errMsg, 705, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);
        else if (elseCasesWhatToDo.equals("")) return "";
        else throw new FunctionException("Invalid elseCasesWhatToDo tag : " + elseCasesWhatToDo + ":" + content, 799, new Throwable(), ApplicationException.SEVERITY_LEVEL_WARNING);

    }

    public List<String> checkMappingFileAndGatheringDomainName(String fileNameSCR) throws FunctionException
    {
        //String domain = "";
        String fileName = "";
        if (fileNameSCR.indexOf(Config.VOCABULARY_MAP_FILE_NAME_DOMAIN_SEPARATOR) > 0)
        {
            String[] arrayRes = extractDomainAndFileName(fileNameSCR);
            //domain = arrayRes[0];
            fileName = arrayRes[1];
        }
        else fileName = fileNameSCR;

        if (type.equals(typeNamePossibleList[0]))
        {
            String mapData ="";
            try {  mapData = checkMappingFileAndExtractData(fileNameSCR); }
            catch(FunctionException fe)
            {
                if (fe.getErrorNumber() == 708) mapData = "";
                else throw fe;
            }

            if (mapData.equals(tagForXMLFormatVOMFile))
            {
                return getDomains(fileName);
            }
        }
        else if (type.equals(typeNamePossibleList[2]))
        {
            return getDomains(fileName);
        }

        List<String[]> listDomainW = new ArrayList<String[]>();
        List<String> listDomain = new ArrayList<String>();
        boolean domainTag = false;
        boolean findTag = false;

        fileName = verifyFileName(fileName);
        File file = new File(fileName);
        if (file.exists())
        {
            if (file.isFile()) {}
            else throw new FunctionException("This vocabulary mapping path is not a file(2). : " + fileName, 702, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        else throw new FunctionException("Not exist vocabulary mapping file(2) : " + fileName, 701, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        String readLineOfFile = "";

        domainList = null;
        try
        {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);

            while((readLineOfFile=br.readLine())!=null)
            {
                readLineOfFile = readLineOfFile.trim();
                if (readLineOfFile.startsWith("#")) continue;


                String startDomain = "&StartDomain:";
                String endDomain = "&EndDomain";
                if (readLineOfFile.toUpperCase().startsWith(startDomain.toUpperCase()))
                {
                    readLineOfFile = readLineOfFile.substring(startDomain.length()).trim();
                    boolean inverseAllowed = true;
                    String inverseNotAllowed = "&InverseNotAllowed:";
                    if (readLineOfFile.toUpperCase().startsWith(inverseNotAllowed.toUpperCase()))
                    {
                        inverseAllowed = false;
                        readLineOfFile = readLineOfFile.substring(inverseNotAllowed.length()).trim();
                    }

                    String domainName = "";
                    String achar = "";
                    for (int i=0;i<readLineOfFile.length();i++)
                    {
                        achar = readLineOfFile.substring(i, i+1);
                        if ((achar.equals(" "))||(achar.equals(";"))||(achar.equals("\t"))) break;
                        domainName = domainName + achar;
                    }
                    if (!domainTag)
                    {
                        if (!domainName.equals(""))
                        {
                            listDomain.add(domainName);

                            if (domainList == null) domainList = new ArrayList<String[]>();
                            String inverse = "true";
                            if (!inverseAllowed) inverse = "false";
                            domainList.add(new String[] {domainName, inverse});
                        }
                    }
                    domainTag = true;
                }
                if (readLineOfFile.toUpperCase().startsWith(endDomain.toUpperCase()))
                {
                    domainTag = false;
                }
                int idx1 = readLineOfFile.indexOf(Config.VOCABULARY_MAP_FILE_VALUE_SEPARATOR);
                if (idx1 > 0) findTag = true;
            }
            fr.close();
            br.close();
        }
        catch(IOException e)
        {
            throw new FunctionException("Vocabulary mapping file reading error(2). : " + value, 703, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        if (!findTag) throw new FunctionException("This is not a Vocabulary Mapping file found. : ", 714, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        return listDomain;
    }


    public void checkFileName(String fileName) throws FunctionException
    {
        //if (fileName.startsWith(FileUtil.getWorkingDirPath())) fileName = fileName.replace(FileUtil.getWorkingDirPath(), Config.CAADAPTER_HOME_DIR_TAG);
        //checkMappingFileAndGatheringDomainName(fileName);

        if (type.equals(typeNamePossibleList[0]))
        {
            String mapData = checkMappingFileAndExtractData(fileName);
            if (mapData.equals(tagForXMLFormatVOMFile))
            {
                getDomains(fileName);
            }
            else checkMappingFileAndGatheringDomainName(fileName);
        }
        else if (type.equals(typeNamePossibleList[2]))
        {
            getDomains(fileName);
        }
    }

    public void setValue(String fileName) throws FunctionException
    {
        if (fileName.startsWith(FileUtil.getWorkingDirPath())) fileName = fileName.replace(FileUtil.getWorkingDirPath(), Config.CAADAPTER_HOME_DIR_TAG);

        if (type.equals(typeNamePossibleList[0]))
        {
            String mapData = checkMappingFileAndExtractData(fileName);
            if (mapData.equals(tagForXMLFormatVOMFile))
            {
                getDomains(fileName);
            }
            else checkMappingFileAndGatheringDomainName(fileName);
        }
        else if (type.equals(typeNamePossibleList[1]))
        {
            //String serviceAddr = serviceField.getText();
            int idx = fileName.indexOf(Config.VOCABULARY_MAP_URL_SEARCH_DATA_INPUT_POINT_CHARACTER);
            //System.out.println("CCC : urlS=" + fileName);
            if (idx < 0)
            {
                boolean foundSourceParameter = false;
                int idx2 = fileName.indexOf("?");
                if(idx2 > 0)
                {
                    String urlS = fileName.substring(0, idx2);
                    String dataLine = fileName.substring(idx2 + 1);
                    java.util.List<String> list = new ArrayList<String>();
                    if(dataLine.indexOf("&") > 0)
                    {
                        StringTokenizer st = new StringTokenizer(dataLine, "&");
                        while(st.hasMoreTokens())
                        {
                            String token = st.nextToken();
                            if (!token.trim().equals("")) list.add(token.trim());
                        }
                    }
                    else list.add(dataLine);

                    String newDataLine = "";
                    for(String data:list)
                    {
                        boolean found = false;
                        int idx3 = data.indexOf("=");
                        if (idx3 < 0) continue;
                        String paramName = data.substring(0, idx3);
                        String paramVal = data.substring(idx3 + 1);
                        if (paramName.equalsIgnoreCase("value")) found = true;
                        if (paramName.equalsIgnoreCase("inputvalue")) found = true;
                        if (paramName.equalsIgnoreCase("inputval")) found = true;
                        if (paramName.equalsIgnoreCase("val")) found = true;
                        if (paramName.equalsIgnoreCase("source")) found = true;
                        if (paramName.equalsIgnoreCase("sourcevalue")) found = true;
                        if (paramName.equalsIgnoreCase("sourceval")) found = true;
                        if (paramName.equalsIgnoreCase("input")) found = true;

                        if ((found)&&(!foundSourceParameter))
                        {
                            foundSourceParameter = true;
                            newDataLine = newDataLine + paramName + "="
                                        + Config.VOCABULARY_MAP_URL_SEARCH_DATA_INPUT_POINT_CHARACTER + paramVal
                                        + Config.VOCABULARY_MAP_URL_SEARCH_DATA_INPUT_POINT_CHARACTER + "&";
                        }
                        else newDataLine = newDataLine + data + "&";
                    }
                    if (newDataLine.endsWith("&")) newDataLine = newDataLine.substring(0, newDataLine.length()-1);
                    if (foundSourceParameter) fileName = urlS + "?" + newDataLine;
                    //break;
                }

                String valueTag = "/value/";
                int idx3 = fileName.toLowerCase().indexOf(valueTag);
                if (idx3 > 0)
                {
                    String tempC = fileName;
                    String part1 = tempC.substring(0, idx3 + valueTag.length());
                    tempC = tempC.substring(idx3 + valueTag.length());
                    int idx4 = tempC.indexOf("/");
                    String part2 = "";
                    String part3 = "";
                    String cc = Config.VOCABULARY_MAP_URL_SEARCH_DATA_INPUT_POINT_CHARACTER;
                    if (idx4 > 0)
                    {
                        part2 = tempC.substring(0, idx4);
                        part3 = tempC.substring(idx4);
                        fileName = part1 + cc + part2 + cc + part3;
                    }
                    else
                    {
                        fileName = part1 + cc + tempC + cc;
                    }
                    foundSourceParameter = true;
                }

                if (!foundSourceParameter)
                {
                    throw new FunctionException("There is no Data input point (marked '"+Config.VOCABULARY_MAP_URL_SEARCH_DATA_INPUT_POINT_CHARACTER+"'). : No Data input point, addr=" + fileName);
                }
            }
        }
        else if (type.equals(typeNamePossibleList[2]))
        {
            getDomains(fileName);
        }

        value = fileName;
    }
    public String searchXMLMappingFile(String input, boolean inversTag, String path) throws FunctionException
    {
        return searchXMLMappingURL(0, input, path, inversTag).get(0);
    }
    public List<String> getDomains(String path) throws FunctionException
    {
        validateVOMdataFile(path);
        return searchXMLMappingURL(1, "", path, false);
    }
    public int getDomainNumber(String path) throws FunctionException
    {
        return getDomains(path).size();
    }
    public String getFirstResult(String path) throws FunctionException
    {
        //validateVOMdataFile(path);
        return searchXMLMappingURL(2, "", path, false).get(0);
    }

    public void validateVOMdataFileWithoutRecord(String path) throws FunctionException
    {
        validateVOMdataFile(path, false);
    }
    private void validateVOMdataFile(String path) throws FunctionException
    {
        validateVOMdataFile(path, true);
    }
    private void validateVOMdataFile(String path, boolean setPathNameJustBeforeValidated) throws FunctionException
    {
        if (type.equals(typeNamePossibleList[1])) return;
        //if (path.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) path = path.replace(Config.CAADAPTER_HOME_DIR_TAG, FileUtil.getWorkingDirPath());
        String[] arrayRes = extractDomainAndFileName(path);
        //String domain = arrayRes[0];
        String pathName = arrayRes[1];
        String targetPath = "";


        pathName = verifyFileName(pathName);
        File file = new File(pathName);
        if (file.exists())
        {
            if (file.isFile()) targetPath = pathName;
        }
        if (targetPath.equals(""))
        {
            try
            {
                targetPath = FunctionUtil.downloadFromURLtoTempFile(pathName);
            }
            catch(IOException ie)
            {
                throw new FunctionException("Invalid path or URL address of vom file ("+pathName+"). : " + ie.getMessage());
            }
        }
        if (setPathNameJustBeforeValidated)
        {
            if (targetPath.equals(pathNameJustBeforeValidated)) return;
        }
        String xsdFilePath = null;
        File aFile = null;

        String xsdFileClassPath = Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION;
        ClassLoaderUtil loader = null;
        String fe = null;

        URL fileURL1 = FileUtil.retrieveResourceURL(xsdFileClassPath);
        if (fileURL1 != null)
        {
            xsdFilePath = fileURL1.toString();
            //System.out.println("CCC path=" + xsdFilePath + ", is=" + setPathNameJustBeforeValidated+", before=" +pathNameJustBeforeValidated);
        }
        else
        {
            String nameS2 = "";
            for (int j=0;j<xsdFileClassPath.length();j++)
            {
                String achar = xsdFileClassPath.substring(j, j+1);
                if ((achar.equals("/"))||(achar.equals(File.separator)))
                {
                    nameS2 = nameS2 + "_";
                }
                else nameS2 = nameS2 + achar;
            }
            File wDir = new File(FileUtil.getUIWorkingDirectoryPath());
            File[] fList = wDir.listFiles();
            for (File ff:fList)
            {
                if (!ff.isFile()) continue;
                String fName = ff.getName();
                if (fName.endsWith(nameS2))
                {
                    xsdFilePath = ff.getAbsolutePath();
                    break;
                }
            }
        }
        if (xsdFilePath == null)
        {
            try
            {
                loader = new ClassLoaderUtil(xsdFileClassPath);//, false);
            }
            catch(IOException ie)
            {
                try
                {
                    loader = new ClassLoaderUtil("map/functions/vom.xsd");//, false);
                }
                catch(IOException ie2)
                {
                    fe = "Not Found xml schema file " + Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION + " for vom file ("+path+") : " + ie.getMessage();
                }
                //fe = "Not Found xml schema file " + Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION + " for vom file ("+path+") : " + ie.getMessage();
            }
            if ((loader == null)||(loader.getFileNames().size() == 0))
            {
                fe = "Not Found xml schema file (2) " + Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION + " for vom file ("+path+")";
            }

            if (fe != null) throw new FunctionException(fe);
            else
            {
                aFile = new File(loader.getFileNames().get(0));
                xsdFilePath = aFile.getAbsolutePath();
            }
        }

        XMLValidator xmlValidator = new XMLValidator(targetPath, xsdFilePath);
        ValidatorResults result = xmlValidator.validate();
        if (!result.isValid())
        {
            String messages = "";
            List<Message> liM = result.getAllMessages();
            for (int i=0;i<liM.size();i++)
            {
                Message mes = liM.get(i);
                messages = messages + "\n" + mes.toString();
            }
            throw new FunctionException("Invalid xml format vom file ("+path+") with " + xsdFilePath + messages);
        }

        if (setPathNameJustBeforeValidated)
        {
            pathNameJustBeforeValidated = targetPath;
        }
    }

    private List<String> searchXMLMappingURL(int jobTag, String searchStr, String path, boolean inversTag) throws FunctionException
    {

        String[] arrayRes = extractDomainAndFileName(path);
        String domain = arrayRes[0];
        String pathName = arrayRes[1];
        //System.out.println("@@ path=" + path + ", domain="+ domain + ", pathName=" + pathName);
        List<String> list = new ArrayList<String>();
        //if (domain.equals("")) domain = defaultDomainName;

        //if (pathName.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) pathName = pathName.replace(Config.CAADAPTER_HOME_DIR_TAG, FileUtil.getWorkingDirPath());

        pathName = verifyFileName(pathName);

        File file = new File(pathName);

        if (file.exists())
        {
            String uri = file.toURI().toString();
            if (uri.toLowerCase().startsWith("file:///")) pathName = uri;
            else if (uri.toLowerCase().startsWith("file://")) pathName = file.toURI().toString().replace("file://", "file:///");
            else if (uri.toLowerCase().startsWith("file:/")) pathName = file.toURI().toString().replace("file:/", "file:///");
        }

        FunctionVocabularyXMLMappingEventHandler handler = null;
        if ((recentVOMFileName != null)&&(recentVOMFileName.equals(pathName)))
        {
            handler = recentVOMHandler;
        }
        else
        {
            //recentVOMHandler = null;
            //recentVOMFileName = null;
            try
            {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();

                XMLReader producer = parser.getXMLReader();
                //ContentHandler handler = new FunctionVocabularyMappingEventHandler();

                handler = new FunctionVocabularyXMLMappingEventHandler();

                producer.setContentHandler(handler);

                producer.parse(new InputSource(pathName.trim()));

            }
            catch(SAXException e)
            {
                throw new FunctionException("XMLMappingEventHandler SAXException (XML) : " + e.getMessage(), 715, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
            }
            catch(ConnectException e)
            {
                throw new FunctionException("XMLMappingEventHandler ConnectException (XML) : " + e.getMessage(), 715, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
            }
            catch(IOException e)
            {
                throw new FunctionException("XMLMappingEventHandler IOException (XML) : " + e.getMessage(), 716, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
            }
            catch(ParserConfigurationException e)
            {
                throw new FunctionException("XMLMappingEventHandler ParserConfigurationException (XML) : " + e.getMessage(), 717, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
            }
            catch(Exception e)
            {
                throw new FunctionException("XMLMappingEventHandler Unknown Exception (XML) : " + e.getMessage(), 717, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
            }
            //System.out.println("DDDD : " + message + " : " +messageLevel + " : " + mappingValue + " : " + handler.getMappingDomain()+ " : " + handler.getMappingSource());
            recentVOMHandler = handler;
            recentVOMFileName = pathName;
        }
        if (jobTag == 0)
        {
            // called by searchXMLMappingFile(String input, boolean inversTag, String path)
            boolean existDomain = false;
            for (String[] strs:handler.getDomains())
            {
                //System.out.println("@@ finding domain("+domain+") : " + strs[0]);
                if (strs[0].equals(domain)) existDomain = true;
            }
            if (!existDomain) throw new FunctionException("This Domain is not exist : " + domain, 717, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

            String lines = handler.getLinesSearchValue(domain);
            //System.out.println("CCCCC Lines=" + lines);
            //System.out.println("CCCCC input=" + searchStr + ", inverse" + inversTag + ", domain=" +domain);
            //System.out.println("CCCCC pathName=" + pathName);
            String rr = searchMappingFile(searchStr, inversTag, lines);
            //System.out.println("CCCCC result=" + rr);
            list.add(rr);
        }
        else if (jobTag == 1)
        {
            // called by getDomains(String path)
            domainList = handler.getDomains();

            for (String[] strs:domainList)
            {
                list.add(strs[0]);
            }
        }
        else if (jobTag == 2)
        {
            // called by getFirstResult(String path)
            list.add(handler.getFirstValue(""));
        }
        return list;
    }
    public List<String[]> getDomainList()
    {
        return domainList;
    }

    public List<String> searchMappingURL(String searchStr) throws FunctionException
    {
        JAXBContext jc = null;
        VocabularyMappingData vmd = null;

        try
        {
        	jc=JAXBContext.newInstance("gov.nih.nci.caadapter.dvts.common.meta");
            Unmarshaller u=jc.createUnmarshaller();

            String addr = modifyURLForSearch(value, searchStr);
            //System.out.println("CCCC addr=" + addr);
            URL url = new URL(addr);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();

//            DataInputStream dis = new DataInputStream(is);
//            String result = "";
//            byte bt = 0;
//
//            while(true)
//            {
//                try { bt = dis.readByte(); }
//                catch(IOException ie) { break; }
//                catch(NullPointerException ie) { break; }
//
//                char cc = (char) bt;
//                result = result + cc;
//            }
//            dis.close();
//            JAXBElement<VocabularyMappingData> jaxbFormula=u.unmarshal(new StreamSource(new CharArrayReader(result.toCharArray())), VocabularyMappingData.class);

            JAXBElement<VocabularyMappingData> jaxbFormula=u.unmarshal(new StreamSource(is), VocabularyMappingData.class);
            //System.out.println("CCCCC : GGGGGG addr="+addr+", result=" + result);

            vmd = jaxbFormula.getValue();

            is.close();
        }
        catch(JAXBException je)
        {
            je.printStackTrace();
            throw new FunctionException("VocMappingEventHandler JAXBException : " + je.getMessage(), 717, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        catch(SocketException e)
        {
            //e.printStackTrace();
            throw new FunctionException("VocMappingEventHandler SocketException : " + e.getMessage(), 713, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw new FunctionException("VocMappingEventHandler IOException : " + e.getMessage(), 721, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        }
        catch(Exception e)
        {
            throw new FunctionException("VocMappingEventHandler Unknown Exception : " + e.getMessage(), 723, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }

        if ((vmd.getReturnMessage().getErrorLevel() == ErrorLevel.ERROR)||
            (vmd.getReturnMessage().getErrorLevel() == ErrorLevel.FATAL))
        {
            throw new FunctionException("Exception on the Server side : " + vmd.getReturnMessage().getValue(), 725, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        if ((vmd.getMappingResults() == null)||
            (vmd.getMappingResults().getResult().size() == 0))
        {
            throw new FunctionException("No translated data from the Server side : ", 726, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        recentVocabularyMappingDataObj = vmd;

        return vmd.getMappingResults().getResult();
    }
    /*
    public List<String> searchMappingURL(String searchStr) throws FunctionException
    {
        String message = "";
        String messageLevel = "";
        List<String> mappingValues = null;

        FunctionVocabularyMappingEventHandler handler = null;
        XMLReader producer = null;
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            producer = parser.getXMLReader();
            //ContentHandler handler = new FunctionVocabularyMappingEventHandler();
            handler = new FunctionVocabularyMappingEventHandler();
            producer.setContentHandler(handler);
            //System.out.println("E1");
            producer.parse(new InputSource(modifyURLForSearch(value, searchStr)));
            //System.out.println("E2");
            message = handler.getMessge();
            //System.out.println("E3");
            messageLevel = handler.getMessgeLevel();
            //System.out.println("E4");
            mappingValues = handler.getMappingResults();
            //System.out.println("EEEE : " + message + " : " +messageLevel + " : " + mappingValue);

        }
        catch(SAXException e)
        {
            throw new FunctionException("VocMappingEventHandler SAXException : " + e.getMessage(), 715, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        catch(ConnectException e)
        {
            throw new FunctionException("VocMappingEventHandler ConnectException : " + e.getMessage(), 715, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        catch(ParserConfigurationException e)
        {
            throw new FunctionException("VocMappingEventHandler ParserConfigurationException : " + e.getMessage(), 717, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw new FunctionException("VocMappingEventHandler IOException : " + e.getMessage(), 716, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        }
//        catch(SocketException e)
//        {
//            e.printStackTrace();
//            throw new FunctionException("VocMappingEventHandler IOException : " + e.getMessage(), 716, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
//
//        }
        catch(Exception e)
        {
            throw new FunctionException("VocMappingEventHandler Unknown Exception : " + e.getMessage(), 717, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
        }
        //System.out.println("DDDD : " + message + " : " +messageLevel + " : " + mappingValue + " : " + handler.getMappingDomain()+ " : " + handler.getMappingSource());
        if (messageLevel.equalsIgnoreCase("Error"))
            throw new FunctionException("MappingEventHandler (Invalid Data) : " + message, 718, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        if ((mappingValues == null)||(mappingValues.size() == 0))
            throw new FunctionException("MappingEventHandler (No output) : No output data returned", 799, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);

        recentURL_VOMHandler = handler;

        return mappingValues;
    }
    */
    public void setType(String typ) throws FunctionException
    {
        boolean check = false;
        String msg = "";
        for (int i=0;i<typeNamePossibleList.length;i++)
        {
            String or = "";
            if (i == 0) or = " ";
            else if (i == (typeNamePossibleList.length-1)) or = " or ";
            else or = ", ";
            if (typeNamePossibleList[i].equals(typ)) check = true;
            msg = msg + or + "'" + typeNamePossibleList[i] + "'";
        }
        if (check)
        {
            type = typ;
        }
        else throw new FunctionException("Vocabulary mapping type must be either" + msg + ". : " + typ, 707, new Throwable(), ApplicationException.SEVERITY_LEVEL_ERROR);
    }
    public void setDefaultWorkDirectory(File dir) throws FunctionException
    {
        if (dir == null) throw new FunctionException("setDefaultWorkDirectory NULL directory");
        if ((!dir.exists())||(!dir.isDirectory())) throw new FunctionException("setDefaultWorkDirectory invalid directory : " + dir.getName());
        defaultWorkDirectory = dir;
    }
    public void setDefaultWorkDirectory(String dir) throws FunctionException
    {
        if (dir == null) throw new FunctionException("setDefaultWorkDirectory NULL directory (String)");
        setDefaultWorkDirectory(new File(dir));
    }
    public String getValue()
    {
        return value;
    }
    public String getType()
    {
        return type;
    }
    public boolean getInverseTag()
    {
        return inverseTag;
    }
    public String getDefaultDomainName()
    {
        return defaultDomainName;
    }
    public String[] getTypeNamePossibleList()
    {
        return typeNamePossibleList;
    }
    public String[] getMethodNamePossibleList()
    {
        return methodNamePossibleList;
    }

    public FunctionVocabularyXMLMappingEventHandler getRecentVOMHandler()
    {
        return recentVOMHandler;
    }
//    public FunctionVocabularyMappingEventHandler getRecentUrlVomHandler()
//    {
//        return recentURL_VOMHandler;
//    }
    public VocabularyMappingData getRecentUrlVomHandler()//getRecentVocabularyMappingDataObj()
    {
        return recentVocabularyMappingDataObj;
    }
    public boolean wasElsecaseApplied()
    {
        return elsecaseApplied;
    }
    private String verifyFileName(String fileName)
    {

        try
        {
            if (defaultWorkDirectory != null) fileName = FileUtil.filenameLocate(defaultWorkDirectory.getAbsolutePath(), fileName);
            else
            {
                if (fileName.startsWith(Config.CAADAPTER_HOME_DIR_TAG)) fileName = fileName.replace(Config.CAADAPTER_HOME_DIR_TAG, FileUtil.getWorkingDirPath());
            }

        }
        catch(FileNotFoundException fe)
        {}
        return fileName;
    }

    private String modifyURLForSearch(String val, String str)
    {
        String sharpChar = Config.VOCABULARY_MAP_URL_SEARCH_DATA_INPUT_POINT_CHARACTER;
        String val1 = "";
        String val2 = "";

        int idx = val.indexOf("=" + sharpChar);
        if (idx < 0) idx = val.indexOf("/" + sharpChar);

        if (idx > 0)
        {
            val1 = val.substring(0, idx + 1);
            val2 = val.substring(idx + 1 + sharpChar.length());
        }
        else
        {
            idx = val.indexOf(sharpChar);
            if (idx > 0)
            {
                val1 = val.substring(0, idx);
                val2 = val.substring(idx + sharpChar.length());
            }
            else return null;
        }


        if (str.equalsIgnoreCase(Config.NULL_VALUE_MARK)) str = "";
        else if (str.equalsIgnoreCase("&nbsp;")) str = " ";
        else if (str.equalsIgnoreCase("&lt;")) str = "<";
        else if (str.equalsIgnoreCase("&gt;")) str = ">";
        else if (str.equalsIgnoreCase("&amp;")) str = "&";

        idx = val2.indexOf(sharpChar);

        if (idx < 0) return val1 + str + val2;//val.replace(sharpChar, str);
        else
        {
            String val3 = val2.substring(idx + sharpChar.length());
            //System.out.println("FFFF : " + val + " : " + val1 + " : "+val3);
            return val1 + str + val3;//val3.replace(sharpChar, str);
        }
    }
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.9  2009/07/19 05:54:11  altturbo
 * HISTORY      : searching "caAdapter.jar" if not found vom.xsd
 * HISTORY      :
 * HISTORY      : Revision 1.8  2009/03/19 02:12:18  altturbo
 * HISTORY      : avoid from occurring errors caused by 'file:/'
 * HISTORY      :
 * HISTORY      : Revision 1.7  2009/03/12 01:40:52  umkis
 * HISTORY      : upgrade for flexibility of vom file location (same directory with the map file)
 * HISTORY      :
 * HISTORY      : Revision 1.6  2008/11/21 16:17:00  wangeug
 * HISTORY      : Move back to HL7 module from common module
 * HISTORY      :
 * HISTORY      : Revision 1.1  2008/11/17 20:07:31  wangeug
 * HISTORY      : Move from HL7 module to common module
 * HISTORY      :
 * HISTORY      : Revision 1.4  2008/06/09 19:53:50  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2008/06/06 18:54:55  phadkes
 * HISTORY      : Changes for License Text
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/10/03 21:54:31  umkis
 * HISTORY      : Removed the problem of calling vom.xsd file from the resource zip file.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 18:26:25  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/11/15 05:47:57  umkis
 * HISTORY      : Fixing Bugs item #3420
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/11/03 19:03:14  umkis
 * HISTORY      : error fixing related to Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/11/02 18:38:05  umkis
 * HISTORY      : XML format vom file must be validated before recorded into a map file with the xml schema file which is directed by Config.VOCABULARY_MAP_XML_FILE_DEFINITION_FILE_LOCATION.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/11/01 02:05:50  umkis
 * HISTORY      : Extending function of vocabulary mapping : URL XML vom file can use.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/10/30 17:02:32  umkis
 * HISTORY      : Change two function names transferMappedValue and transferInverseMappedValue to translateValue and inverseTranslateValue.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/10/11 19:17:57  umkis
 * HISTORY      : error fixing related to {caAdapter_Home} tag.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/10/11 18:37:40  umkis
 * HISTORY      : protect inputting 'URL' type when inverse mapping.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/10/02 20:17:47  umkis
 * HISTORY      : error correction from 'tranferMappedValue' to 'transferMappedValue'
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/10/02 18:05:08  umkis
 * HISTORY      : Vocabulary mapping function upgrade which allow to mapping through a URL and domained .vom file.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/09/08 20:19:12  umkis
 * HISTORY      : When searching is failure, error message is differentiated whether inverse searching or forward.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/09/06 23:17:05  umkis
 * HISTORY      : add Inverse Else case
 * HISTORY      :
 * HISTORY      : Revision 1.4  2006/09/06 21:34:21  umkis
 * HISTORY      : The file path of source file change expression from absolute path to relative such as {caAdapter_Home}\workingspace.....
 * HISTORY      :
 * HISTORY      : Revision 1.3  2006/09/06 19:50:09  umkis
 * HISTORY      : The file path of source file change expression from absolute path to relative such as {caAdapter_Home}\workingspace.....
 * HISTORY      :
 * HISTORY      : Revision 1.2  2006/09/06 18:52:31  umkis
 * HISTORY      : The new implement of Vocabulary Mapping function.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/09/06 18:20:35  umkis
 * HISTORY      : The new implement of Vocabulary Mapping function.
 * HISTORY      :
 */
