//package com.sharp.ccpe.controller;
//
////import com.sharp.ccpe.controller.helper.MemberExistenceCheck;
////import com.sharp.ccpe.parsing.Constants;
////import com.sharp.ccpe.utils.Utility;
////import org.apache.log4j.//loger;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.xpath.XPathExpressionException;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
////import static com.sharp.ccpe.utils.Utility.*;
//
///*
// * This method is used for processing ID cards extracts and generating the custom XML for O'Neil
// * TSC-0006		TPL-H131-B		ID Card - Off Exchange (Access Dental)
// * TSC-0006		TPL-H131-C		ID Card - Off Exchange
// * TSC-0006		TPL-H131-D		ID Card - On Exchange
// * TSC-0006		TPL-H131-E		ID Card - POS
// * TSC-0006		TPL-H131-F		ID Card - MEA
// * TSC-0006		TPL-H131-J		ID Card - CalPERS Companion Card
// */
//
////public class IDCardsController extends GenericController implements Constants {
//public class IDCardsController {
//
////    private static //loger //loger = //loger.get//loger(IDCardsController.class);
//    private Map<String, Map<String, List<String>>> subscribersMap = new LinkedHashMap<>();
//    private String deDupDocType = "Member ID Card - Member Request | Member ID Card - Portal Member Request | Member ID Card - Demographic Change";
//    private Map<String, String> cancelledExtractsMap = new LinkedHashMap<>();
//    private static Map<String,String> PMGIdAndNameMap = new HashMap<>();
//    static {
//        PMGIdAndNameMap.put("GTC","Greater Tri-Cities IPA");
//        PMGIdAndNameMap.put("IND","Independent");
//        PMGIdAndNameMap.put("IND - Encompass","Independent");
//        PMGIdAndNameMap.put("PCAMG","Primary Care Associates Medical Group");
//        PMGIdAndNameMap.put("RCHN","Rady Children's Health Network/CPMG");
//        PMGIdAndNameMap.put("SCMG","Sharp Community Medical Group");
//        PMGIdAndNameMap.put("SCMG-Arch","SCMG Arch Health Medical Group");
//        PMGIdAndNameMap.put("SCMG-Graybill","SCMG Graybill");
//        PMGIdAndNameMap.put("SCMG-Inland","SCMG Inland North");
//        PMGIdAndNameMap.put("SRS","Sharp Rees-Stealy Medical Group");
//    }
//    public void process() throws Exception {
////        currProcess = ID_CARDS_PROCESS;
////        validateAndWrapCustomers();
//    }
//    public void processIDCardsMerging(String[] filenames) {
//        try {
//            System.out.println("Begin Processing ID Cards ::: ");
//            //loger.info(ID_CARDS_PROCESS + " (processIDCardssMerging) processing started.");
//
//            // Remove Bad Files
//            //loger.info("Begin Validating Files...");
//            List<String> xmlFileList = listXmlFiles(filenames);
//            //loger.info("Completed Validating Files...");
//
//            // Remove DeDup Doc Types
//            //loger.info("Begin Removing DeDup Doc Types...");
//            List<String> noDuplicatesFileList = removeDuplicateFiles(xmlFileList);
//            //loger.info("Completed Removing DeDup Doc Types...");
//
//            // Group Files by Member ID and Filename
//            //loger.info("Begin Grouping Files...");
//            groupSubscribersFiles(noDuplicatesFileList);
//            //loger.info("Completed Grouping Files...");
//
//            //loger.info(ID_CARDS_PROCESS + " (processIDCardsMerging) processing completed.");
//        } catch (Exception e) {
//            //loger.error("An error occurred while processIDCardsMerging process", e);
//        }
//    }
//    private List<String> listXmlFiles(String[] filenames) {
//        List<String> filenameList = new ArrayList<>();
//        for (String filename : filenames) {
//            if (filename.endsWith(".xml")) {
//                filenameList.add(filename);
//            } else {
//                errorHandling(filename);
//            }
//        }
//        return  filenameList;
//    }
//    private void errorHandling(String filename) {
//        // Keep Track of Bad Files
//        //loger.info("Adding '" + filename + "' to filesWithIssues List!" );
////        filesWithIssues.add(filename);
////        errorFileDesc.append(filename).append(" ::: ").append("file is not an xml.");
////        errorFileDesc.append("Error is ").append("file is not an xml.");
////        errorFileDesc.append(newLine);
//
//        //loger.error("Error occurred while processing '" + filename + "': file is not an xml.");
//        System.out.println("Error occurred while processing '" + filename + "': file is not an xml.");
//    }
//    private List<String> removeDuplicateFiles(List<String> filenameList) {
//
//        List<String> noDupesList = new ArrayList<>();
//        Map<String, String> nonDeDupDocTypes = new TreeMap<>();
//
//        for (String filename : filenameList) {
//
//            try {
//
//                // Create Input XML Document
//                Document inputDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(folderPath + fileSeparator + filename));
//                System.out.println("Processing File: " + folderPath + fileSeparator + filename);
//                //loger.info("Processing File: " + folderPath + fileSeparator + filename);
//
//                // Cleanse Document and Get correspondenceItemList Node
//                Node correspondenceItemListNode = cleanseNode(inputDoc);
//
//                // Get Member ID
//                String idXpath = "correspondenceItem/recipient/member/id";
//                String id = getNodeText(idXpath, correspondenceItemListNode);
//                if (id.equals(""))
//                    throw new Exception("Required 'id' Node is missing at " + idXpath);
//
//                // Get Document Type
//                String docTypeXpath = "correspondenceItem/documentType";
//                String docType = getNodeText(docTypeXpath, correspondenceItemListNode);
//                if (docType.equals(""))
//                    throw new Exception("Required 'documentType' Node is missing at " + docTypeXpath);
////				System.out.println("documentType: " + docType);
////				//loger.info("documentType: " + docType);
//
//                // Check for DeDup Doc Types to the Cancelled Extracts Map
//                if (deDupDocType.contains(docType)) {
//
////					System.out.println("DeDup DocType: true");
////					//loger.info("DeDup DocType: true");
//
//                    if (nonDeDupDocTypes.containsKey(id + docType)) {
//
//                        System.out.println("DeDup Doc Type Existing File Found :: Removing from List");
//                        //loger.info("DeDup Doc Type Existing File Found :: Removing from List");
//
//                        // Add DeDup File to Cancelled Extracts Map
//                        String correspondenceIdXpath = "correspondenceItem/correspondenceId";
//                        String correspondenceId = getNodeText(correspondenceIdXpath, correspondenceItemListNode);
//                        cancelledExtractsMap.put(correspondenceId, id);
//
//                        // Add to Cancelled Extract Map
//                        //loger.info("Cancelling Dedup Card for corrId: " + correspondenceId + " ; docType: " + id + " ; filename: " + folderPath + fileSeparator + filename);
//                        //loger.info("Adding '" + filename + "' to filesWithIssues List!" );
//                        filesWithIssues.add(filename);
//                        errorFileDesc.append(filename + " ::: file is a dedup for corrId: " + correspondenceId + " ; memberId: " + id);
//
//                    } else {
//
////						System.out.println("No Existing File Found :: Adding to List");
////						//loger.info("No Existing File Found :: Adding to List");
//                        nonDeDupDocTypes.put(id + docType, filename);
//                        noDupesList.add(filename);
//                    }
//                } else {
//
////					System.out.println("DeDup DocType: false");
////					//loger.info("DeDup DocType: false");
//                    nonDeDupDocTypes.put(id + docType, filename);
//                    noDupesList.add(filename);
//                }
//            } catch (Exception e) {
//                //loger.info("Adding '" + filename + "' to filesWithIssues List!" );
//                filesWithIssues.add(filename);
//                errorFileDesc.append(filename + " ::: not able to parse the xml file. XML format may be incorrect.");
//                if (e.getMessage() != null && !"".equals(e.getMessage())) {
//                    errorFileDesc.append("Error is ").append(e.getMessage());
//                }
//                errorFileDesc.append(newLine);
//
//                System.out.println("Error occurred while processing '" + filename + "': ");
//                e.printStackTrace();
//                //loger.error("Error occurred while processing '" + filename + "': ", e);
//            }
//
//        }
//        //loger.info("noDupesList size : " + noDupesList.size());
//        List<String> fileNamesOrderedList = new ArrayList<>(nonDeDupDocTypes.values());
//        //return noDupesList;
//        return fileNamesOrderedList;
//    }
//    private void groupSubscribersFiles(List<String> filenameList) {
//        for (String filename : filenameList) {
//            try {
//
//                // Create Input XML Document
//                Document inputDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
//                        .parse(new File(folderPath + fileSeparator + filename));
//
//                // Cleanse Document and Get correspondenceItemList Node
//                Node correspondenceItemListNode = cleanseNode(inputDoc);
//                System.out.println("Root XML Node: " + correspondenceItemListNode.getNodeName());
//                //loger.info("Root XML Node: " + correspondenceItemListNode.getNodeName());
//
//                // Remove Extract if member id starts with a 'S' (Medicare)
//                String memberIdXpath = "correspondenceItem/recipient/member/id";
//                String memberId = getNodeText(memberIdXpath, correspondenceItemListNode);
//                if (memberId.startsWith("S"))
//                    throw new Exception("Member ID starts with S which means this is a Medicare account, removing from run "
//                            + memberId);
//                System.out.println("memberId: " + memberId);
//                //loger.info("memberId: " + memberId);
//
//                // Get Subscription ID
//                String subscriptionIdXpath = "correspondenceItem/recipient/member/subscriptionId";
//                String subscriptionId = getNodeText(subscriptionIdXpath, correspondenceItemListNode);
//                if (subscriptionId.equals(""))
//                    throw new Exception("Required 'subscriptionId' Node is missing at " + subscriptionIdXpath);
//                System.out.println("subscriptionId: " + subscriptionId);
//                //loger.info("subscriptionId: " + subscriptionId);
//
//                // Get Correspondence Address Info
//                String addressXpath = "correspondenceItem/recipient/member/correspondenceAddress/address";
//                String address1 = getNodeText(addressXpath, correspondenceItemListNode);
////				if (address1.equals(""))
////					throw new Exception("Required 'address' Node is missing at " + addressXpath);
//
//                String address2Xpath = "correspondenceItem/recipient/member/correspondenceAddress/address2";
//                String address2 = getNodeText(address2Xpath, correspondenceItemListNode);
//
//                String address3Xpath = "correspondenceItem/recipient/member/correspondenceAddress/address3";
//                String address3 = getNodeText(address3Xpath, correspondenceItemListNode);
//
//                String cityXpath = "correspondenceItem/recipient/member/correspondenceAddress/city";
//                String city = getNodeText(cityXpath, correspondenceItemListNode);
////				if (city.equals(""))
////					throw new Exception("Required 'city' Node is missing at " + cityXpath);
//
//                String countryCodeXpath = "correspondenceItem/recipient/member/correspondenceAddress/countryCode";
//                String countryCode = getNodeText(countryCodeXpath, correspondenceItemListNode);
//
//                String countyCodeXpath = "correspondenceItem/recipient/member/correspondenceAddress/countyCode";
//                String countyCode = getNodeText(countyCodeXpath, correspondenceItemListNode);
//
//                String stateCodeXpath = "correspondenceItem/recipient/member/correspondenceAddress/stateCode";
//                String stateCode = getNodeText(stateCodeXpath, correspondenceItemListNode);
////				if (stateCode.equals(""))
////					throw new Exception("Required 'stateCode' Node is missing at " + stateCodeXpath);
//
//                String zipCodeXpath = "correspondenceItem/recipient/member/correspondenceAddress/zipCode";
//                String zipCode = getNodeText(zipCodeXpath, correspondenceItemListNode);
////				if (zipCode.equals(""))
////					throw new Exception("Required 'zipCode' Node is missing at " + zipCodeXpath);
//
//                String zipExtensionXpath = "correspondenceItem/recipient/member/correspondenceAddress/zipExtension";
//                String zipExtension = getNodeText(zipExtensionXpath, correspondenceItemListNode);
//
//                String addressCSV = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
//                        address1, address2, address3, city, countryCode, countyCode, stateCode, zipCode, zipExtension);
//                System.out.println("Address: " + addressCSV);
//                //loger.info("Address: " + addressCSV);
//
//                // Group Subscriber Files
//                if (subscribersMap.containsKey(subscriptionId)) {
//
//                    // Get Existing Subscribers Address
//                    Map<String, List<String>> existingSubscriberMap = subscribersMap.get(subscriptionId);
//
//                    // Compare Addresses
//                    if (existingSubscriberMap.containsKey(addressCSV)) {
//
//                        // Member has Same Address as Subscriber
//                        List<String> subscribersAddressList = existingSubscriberMap.get(addressCSV);
//
//                        // Add Filename to Existing Subscribers Address List
//                        subscribersAddressList.add(folderPath + fileSeparator + filename);
//
//                        // Put Subscriber Address and Filename in Map
//                        existingSubscriberMap.put(addressCSV, subscribersAddressList);
//
//                    } else {
//
//                        // Member has Different Address as Subscriber
//                        List<String> newAddressList = new ArrayList<>();
//
//                        // Create New Address List
//                        newAddressList.add(folderPath + fileSeparator + filename);
//
//                        // Put Subscriber Address and Filename in Map
//                        existingSubscriberMap.put(addressCSV, newAddressList);
//                    }
//
//                    // Add File to Existing Subscriber
//                    subscribersMap.put(subscriptionId, existingSubscriberMap);
//
//                } else {
//
//                    // New Subscriber
//                    List<String> fileNameList = new ArrayList<>();
//
//                    // Create New Address List
//                    fileNameList.add(folderPath + fileSeparator + filename);
//
//                    // Create New Address Map
//                    Map<String, List<String>> newSubscriberMap = new LinkedHashMap<>();
//                    newSubscriberMap.put(addressCSV, fileNameList);
//
//                    // Add New Subscriber to Subscriber Map
//                    subscribersMap.put(subscriptionId, newSubscriberMap);
//                }
//            } catch (Exception e) {
//                //loger.info("Adding '" + folderPath + fileSeparator + filename + "' to filesWithIssues List!" );
//                filesWithIssues.add(filename);
//                for (int i=0; i<filesWithIssues.size(); i++) {
//                    //loger.info(filesWithIssues.get(i));
//                }
//                errorFileDesc.append(folderPath + fileSeparator + filename).append(" ::: ").append("not able to parse the xml file. XML format may be incorrect.");
//                if (e.getMessage() != null && !"".equals(e.getMessage())) {
//                    errorFileDesc.append("Error is ").append(e.getMessage());
//                }
//                errorFileDesc.append(newLine);
//
//                //loger.error("Error occurred while processing '" + filename + "': ", e);
//                System.out.println("Error occurred while processing '" + filename + "': ");
//                e.printStackTrace();
//            }
//        }
//    }
//    public void processFiles(String[] files) throws Exception {
//
//        //loger.info("ID Cards processing started.");
//
//        // Load Properties
//        Properties props = new Properties();
//        String fileSeparator = System.getProperty("file.separator");
//        if (null == fileSeparator || "".equals(fileSeparator)) fileSeparator = FILE_SEPARATOR_IF_NOT_FOUND;
//        InputStream propStream = this.getClass().getClassLoader().getResourceAsStream("config.properties");
//        try {
//            props.load(propStream);
//        } catch (IOException e1) {
//            //loger.error("Unable to load config.properties file: ", e1);
//            e1.printStackTrace();
//            throw new Exception(e1.getMessage());
//        }
//
//        // Get ID Card Values from Config Properties File
//        List<String> visionRiderList = getRiderList(props, "idcard.visionRider");
//        List<String> mentalHealthRiderList = getRiderList(props, "idcard.mentalHealthRider");
//        List<String> pharmacyRiderList = getRiderList(props, "idcard.pharmacyRider");
//        List<String> chiroAcupunctureRiderList = getRiderList(props, "idcard.chiroAcupunctureRider");
//        String memberExistenceCheckFlag = props.getProperty("idcards.memberExistenceCheckFlag");
//
//        // Create Sets
//        Set<String> definitionSet = null;
//        Set<String> initialCardRunSet = null;
//
//        // Create Lists
//        List<Map> delimitedFileDataList = new ArrayList<>();
//
//        // Set Document Builder Factory Name Space Aware to TRUE
//        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//        documentBuilderFactory.setNamespaceAware(true);
//
//        // Create Custom XML for O'Neil
//        Document oNeilXmlDoc = null;
//        try {
//            oNeilXmlDoc = documentBuilderFactory.newDocumentBuilder().newDocument();
//        } catch (ParserConfigurationException e1) {
//            e1.printStackTrace();
//        }
//
//        // Create XML Document for Updater Config Response File
//        Document updaterConfigXmlDoc = null;
//        try {
//            updaterConfigXmlDoc = documentBuilderFactory.newDocumentBuilder().newDocument();
//        } catch (ParserConfigurationException e1) {
//            e1.printStackTrace();
//        }
//
//        // Create O'Neil XML Root Node
//        Node oNeilRootNode_memberIdCards = oNeilXmlDoc.createElement("memberIdCards");
//        oNeilXmlDoc.appendChild(oNeilRootNode_memberIdCards);
//
//        // Create Time Stamp
//        Date timestamp = new Date();
//        DateFormat filenameTimeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
//        String filenameTimeStamp = filenameTimeStampFormat.format(timestamp);
//
//        // Set O'Neil XML Filename
//        idcardsOutputFileName = "idcards_" + filenameTimeStamp + ".xml";
//
//        // Create XML Filename Node
//        Node healthRulesFileNameNode = oNeilXmlDoc.createElement("healthRulesFileName");
//        healthRulesFileNameNode.setTextContent(idcardsOutputFileName);
//        oNeilRootNode_memberIdCards.appendChild(healthRulesFileNameNode);
//
//        // Create Time Stamp Node
//        Node fileCreateDateTimeNode = oNeilXmlDoc.createElement("fileCreateDateTime");
//        DateFormat xmlNodeTimeStampFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:SS");
//        fileCreateDateTimeNode.setTextContent(xmlNodeTimeStampFormat.format(timestamp));
//        oNeilRootNode_memberIdCards.appendChild(fileCreateDateTimeNode);
//
//        // Create Total Carriers Node
//        Node totalCarriersNode = oNeilXmlDoc.createElement("totalCarriers");
//        totalCarriersNode.setTextContent(""); // this we will set at the end all the carriers
//        oNeilRootNode_memberIdCards.appendChild(totalCarriersNode);
//
//        // Create Root Node correspondences for Updater Config XML
//        Node updaterConfigRootNode_correspondences = updaterConfigXmlDoc.createElementNS(
//                "http://www.healthedge.com/connector/schema/correspondence/updater",
//                "n:correspondences");
//        updaterConfigXmlDoc.appendChild(updaterConfigRootNode_correspondences);
//
//        // Create jobType Node for Updater Config XML
//        Node jobTypeNode = updaterConfigXmlDoc.createElement("jobType");
//        jobTypeNode.setTextContent("membership");
//        updaterConfigRootNode_correspondences.appendChild(jobTypeNode);
//
//        // Create configName Node for Updater Config XML
//        Node configNameNode = updaterConfigXmlDoc.createElement("configName");
//        configNameNode.setTextContent("All");
//        updaterConfigRootNode_correspondences.appendChild(configNameNode);
//
//        // Iterate through Hash Map - Map<String, Map<String, List<String>>>
//        for (Map.Entry o : subscribersMap.entrySet()) {
//
//            // Get Map Entry
//            Map.Entry pair1 = o;
//            String subscriptionID = (String) pair1.getKey();
//            Map<String, List<String>> addressAndFileNameMap = (Map<String, List<String>>) pair1.getValue();
//            Iterator it2 = addressAndFileNameMap.entrySet().iterator();
//
//            //loger.info(" ---> subscriptionID : " + subscriptionID + " ; Files are below: ");
//
//            while (it2.hasNext()) {
//
//                Map.Entry pair2 = (Map.Entry) it2.next();
//                List<String> memberFilesList = (List<String>) pair2.getValue();
//
//                //loger.info(" ---> fileNamList Size : " + memberFilesList.size());
//
//                // Start New Envelop
//                int envelopeCount = 0;
//                String name = "";
//
//                Node envelopeNode = oNeilXmlDoc.createElement("envelope");
//                Node carrierNode = oNeilXmlDoc.createElement("carrier");
//
//                //loger.info("Creating letterIDNode");
//                Node letterIDNode;
//
//                int memberAge = 0;
//
//                for (int memberCount = 0; memberCount < memberFilesList.size(); memberCount++) {
//
//                    // Create Delimited File Data Map and Variables
//                    Map<String, String> delimitedFileDataMap = new HashMap<>();
//                    String topAccountName = "";
//                    String address = "";
//                    String city = "";
//                    String stateCode = "";
//                    String completeZip = "";
//                    String memberFullName;
//                    String isSubscriber;
//                    String memberId;
//                    String birthDate;
//                    String product;
//                    String effectiveDate;
//                    String benefitPlanDesc;
//                    String benefitPlanId;
//
//                    String documentType;
//
//                    Node parentOfMessageNode = null;
//                    Node nameNode = null;
//                    Node addressNode = null;
//                    Node cityNode = null;
//                    Node stateNode = null;
//                    Node zipCodeNode = null;
//
//                    if (envelopeCount == 0) {
//
//                        Node mailingAddressNode = oNeilXmlDoc.createElement("mailingAddress");
//                        carrierNode.appendChild(mailingAddressNode);
//
//                        parentOfMessageNode = oNeilXmlDoc.createElement("parentOfMessage");
//                        mailingAddressNode.appendChild(parentOfMessageNode);
//
//                        nameNode = oNeilXmlDoc.createElement("Name");
//                        mailingAddressNode.appendChild(nameNode);
//
//                        addressNode = oNeilXmlDoc.createElement("address");
//                        mailingAddressNode.appendChild(addressNode);
//
//                        cityNode = oNeilXmlDoc.createElement("city");
//                        mailingAddressNode.appendChild(cityNode);
//
//                        stateNode = oNeilXmlDoc.createElement("state");
//                        mailingAddressNode.appendChild(stateNode);
//
//                        zipCodeNode = oNeilXmlDoc.createElement("zipCode");
//                        mailingAddressNode.appendChild(zipCodeNode);
//                    }
//
//                    File f = new File(memberFilesList.get(memberCount));
//
//                    totalFilesCount++;
//
//                    try {
//                        //loger.info(" ---> " + f.getName());
//
//                        // Parse Input XML Document
//                        Document idCardInputXmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f.getAbsolutePath());
//
//                        // Cleanse XML and Get correspondenceItemList Root Node
//                        Node correspondenceItemListNode = cleanseNode(idCardInputXmlDoc);
//
//                        try {
//
//                            //loger.info("totalFilesCount: " + totalFilesCount);
//
//                            //loger.info("envelopeCount: " + envelopeCount);
//
//                            if (envelopeCount == 0) {
//
//                                //loger.info("Getting first envelope variables...");
//
//                                definitionSet = new LinkedHashSet<>();
//                                initialCardRunSet = new LinkedHashSet<>();
//
//                                // Get Name of First Member
//                                if (memberCount == 0) {
//
//                                    String firstNameXpath = "correspondenceItem/recipient/member/firstName";
//                                    String firstName = getNodeText(firstNameXpath, correspondenceItemListNode);
//
//                                    String middleNameXpath = "correspondenceItem/recipient/member/middleName";
//                                    String middleName = getNodeText(middleNameXpath, correspondenceItemListNode);
//                                    middleName = (middleName.equals("")) ? "" : " " + middleName.substring(0, 1);
//
//                                    String lastNameXpath = "correspondenceItem/recipient/member/lastName";
//                                    String lastName = getNodeText(lastNameXpath, correspondenceItemListNode);
//
//                                    String nameSuffixXpath = "correspondenceItem/recipient/member/nameSuffix";
//                                    String nameSuffix = getNodeText(nameSuffixXpath, correspondenceItemListNode);
//                                    nameSuffix = (nameSuffix.equals("")) ? "" : " " + nameSuffix;
//
//                                    name = firstName + middleName + " " + lastName + nameSuffix;
//
//                                    String birthDateXpath = "correspondenceItem/recipient/member/birthDate";
//                                    birthDate = getNodeText(birthDateXpath, correspondenceItemListNode);
//                                    memberAge = Utility.calculateAge(birthDate, "yyyy-MM-dd");
//
//                                }
//                                if (memberAge < 18) {
//                                    parentOfMessageNode.setTextContent("To the Parent/Guardian of:");
//                                } else {
//                                    parentOfMessageNode.setTextContent("");
//                                }
//
//                                nameNode.setTextContent(name);
//
//                                String topAccountXpath = "correspondenceItem/recipient/member/hierarchialAccountInformation/topAccountName";
//                                topAccountName = getNodeText(topAccountXpath, correspondenceItemListNode);
//
//                                // Get Correspondence Address Info
//                                String addressXpath = "correspondenceItem/recipient/member/correspondenceAddress/address";
//                                String address1 = getNodeText(addressXpath, correspondenceItemListNode);
//
//                                String address2Xpath = "correspondenceItem/recipient/member/correspondenceAddress/address2";
//                                String address2 = getNodeText(address2Xpath, correspondenceItemListNode);
//
//                                String address3Xpath = "correspondenceItem/recipient/member/correspondenceAddress/address3";
//                                String address3 = getNodeText(address3Xpath, correspondenceItemListNode);
//
//                                address = address1 + address2 + address3;
//                                addressNode.setTextContent(address);
//
//                                String cityXpath = "correspondenceItem/recipient/member/correspondenceAddress/city";
//                                city = getNodeText(cityXpath, correspondenceItemListNode);
//                                cityNode.setTextContent(city);
//
//                                String stateCodeXpath = "correspondenceItem/recipient/member/correspondenceAddress/stateCode";
//                                stateCode = getNodeText(stateCodeXpath, correspondenceItemListNode);
//                                stateNode.setTextContent(stateCode);
//
//                                String zipCodeXpath = "correspondenceItem/recipient/member/correspondenceAddress/zipCode";
//                                String zipCode = getNodeText(zipCodeXpath, correspondenceItemListNode);
//
//                                String zipExtensionXpath = "correspondenceItem/recipient/member/correspondenceAddress/zipExtension";
//                                String zipExtension = getNodeText(zipExtensionXpath, correspondenceItemListNode);
//                                zipExtension = (zipExtension.equals("")) ? "" : "-" + zipExtension;
//
//                                completeZip = zipCode + zipExtension;
//                                zipCodeNode.setTextContent(completeZip);
//
//                                //loger.info("End getting first envelope variables...");
//                            }
//
//                            // delimitedFileDataMap.put("Member Name", name);
//                            //loger.info("Add values to delimited file data map");
//                            delimitedFileDataMap.put("Employer Group Name", topAccountName);
//                            delimitedFileDataMap.put("Correspondence Address", address);
//                            delimitedFileDataMap.put("Correspondence City", city);
//                            delimitedFileDataMap.put("Correspondence State", stateCode);
//                            delimitedFileDataMap.put("Correspondence Zip", completeZip);
//                            //loger.info("Address: " + address + " " + city + " " + stateCode + " " + completeZip);
//
//                            //loger.info("Set output node values");
//                            Node idCardNode = oNeilXmlDoc.createElement("idCard");
//
//                            Node cardNumberNode = oNeilXmlDoc.createElement("cardNumber");
//                            cardNumberNode.setTextContent("0" + (envelopeCount + 1));
//
//                            Node cardTemplateNode = oNeilXmlDoc.createElement("cardTemplate");
//
//                            String firstNameXpath = "correspondenceItem/recipient/member/firstName";
//                            String firstName = getNodeText(firstNameXpath, correspondenceItemListNode);
//
//                            String middleNameXpath = "correspondenceItem/recipient/member/middleName";
//                            String middleName = getNodeText(middleNameXpath, correspondenceItemListNode);
//                            middleName = (middleName.equals("")) ? "" : " " + middleName.substring(0, 1);
//
//                            String lastNameXpath = "correspondenceItem/recipient/member/lastName";
//                            String lastName = getNodeText(lastNameXpath, correspondenceItemListNode);
//
//                            String nameSuffixXpath = "correspondenceItem/recipient/member/nameSuffix";
//                            String nameSuffix = getNodeText(nameSuffixXpath, correspondenceItemListNode);
//                            nameSuffix = (nameSuffix.equals("")) ? "" : " " + nameSuffix;
//
//                            memberFullName = firstName + middleName + " " + lastName + nameSuffix;
//
//                            Node memberFullNameNode = oNeilXmlDoc.createElement("memberFullName");
//                            memberFullNameNode.setTextContent(memberFullName);
//
//                            delimitedFileDataMap.put("Member Name", memberFullName);
//
//                            String isSubscriberXpath = "correspondenceItem/recipient/member/isSubscriber";
//                            isSubscriber = getNodeText(isSubscriberXpath, correspondenceItemListNode);
//
//                            delimitedFileDataMap.put("isSubscriber", isSubscriber);
//
//                            String memberIdXpath = "correspondenceItem/recipient/member/id";
//                            memberId = getNodeText(memberIdXpath, correspondenceItemListNode);
//
//                            Node memberIdNode = oNeilXmlDoc.createElement("memberId");
//                            memberIdNode.setTextContent(memberId);
//
//                            delimitedFileDataMap.put("Member ID", memberId);
//
//                            String birthDateXpath = "correspondenceItem/recipient/member/birthDate";
//                            birthDate = getNodeText(birthDateXpath, correspondenceItemListNode);
//
//                            Node DOBNode = oNeilXmlDoc.createElement("DOB");
//                            DOBNode.setTextContent(birthDate);
//
//
//                            delimitedFileDataMap.put("Member Birth Date", birthDate);
//
//                            String genderCodeXpath = "correspondenceItem/recipient/member/genderCode";
//                            String genderCode = getNodeText(genderCodeXpath, correspondenceItemListNode);
//
//                            Node sexNode = oNeilXmlDoc.createElement("sex");
//                            sexNode.setTextContent(genderCode);
//
//                            //loger.info("Get latest plan node");
//                            // Get all End Date Nodes from currentPlans, pendingPlans and historicalPlans
//                            TreeMap<Date, Node> planTreeMap = new TreeMap<>(Collections.reverseOrder());
//                            String[] planNodeNames = {"currentPlans", "pendingPlans", "historicalPlans"};
//                            for (String planName : planNodeNames) {
//                                String expression = "correspondenceItem/recipient/member/" + planName + "/planInformation/endDate";
//                                NodeList planNodeList = getNodeList(expression, correspondenceItemListNode);
//                                if (planNodeList != null) {
//                                    for (int i = 0; i < planNodeList.getLength(); i++) {
//                                        Date key = new SimpleDateFormat("yyyy-MM-dd").parse(planNodeList.item(i).getTextContent());
//                                        if (planTreeMap.containsKey(key)) {
//                                            //throw new Exception("Duplicate End Date in planInformation Node: " + planName);
//                                        } else{
//                                            Element planInformationElement = (Element) planNodeList.item(i).getParentNode();
//                                            String startDateText = planInformationElement.getElementsByTagName("startDate").item(0).getTextContent();
//                                            String endDateText = planInformationElement.getElementsByTagName("endDate").item(0).getTextContent();
//                                            if (!startDateText.equals(endDateText))
//                                                planTreeMap.put(key, planNodeList.item(i).getParentNode());
//                                        }
//                                    }
//                                } else {
//                                    throw new Exception("No currentPlans or pendingPlans or historicalPlans Nodes");
//                                }
//                            }
//
//                            // New Method
//                            Date latestDate = new SimpleDateFormat("yyyy-MM-dd").parse("3000-01-01");
//                            Node latestPlanNode = null;
//                            for (Date key : planTreeMap.keySet()) {
//                                if (key.equals(latestDate)) {
//                                    // If endDate is 3000-01-01 check if Start Date is < 30 Days from Now
//                                    Element planInformationElement = (Element) planTreeMap.get(key);
//                                    String startDateText = planInformationElement.getElementsByTagName("startDate").item(0).getTextContent();
//                                    Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateText);
//                                    Calendar c = Calendar.getInstance();
//                                    c.setTime(new Date());
//                                    c.add(Calendar.DATE, 30);
//                                    Date compareDate = c.getTime();
//                                    //loger.info("startDate: " + startDateText + " " + startDate.toString());
//                                    //loger.info("compareDate: " + compareDate.toString());
//                                    String productName = getNodeText("product/productName", planTreeMap.get(key));
//                                    if (!productName.equals("Medicare Advantage")) {
//                                        if (startDate.before(compareDate)) {
//                                            latestPlanNode = planTreeMap.get(key);
//                                            break;
//                                        }
//                                    } else {
//                                        throw new Exception("Plan with 3000-01-01 End Date is a Medicare Advantage plan :: suppressing this ID Card");
//                                    }
//                                } else {
//                                    latestPlanNode = planTreeMap.get(key);
//                                    break;
//                                }
//                            }
//                            if (latestPlanNode == null)
//                                throw new Exception("Error: No valid endDates found in currentPlans/pendingPlans/historicalPlans");
//
//                            effectiveDate = getNodeText("startDate", latestPlanNode);
//                            benefitPlanDesc = getNodeText("planName", latestPlanNode);
//                            benefitPlanId = getNodeText("planId", latestPlanNode);
//                            String networkXpath = "benefitNetworkList/benefitNetworkInformation/benefitNetworkName";
//                            String network = getNodeText(networkXpath, latestPlanNode);
//                            product = getNodeText("product/productName", latestPlanNode);
//
//                            delimitedFileDataMap.put("Product", product);
//
//                            //loger.info("Get Riders");
//                            List<String> riderNameList = new ArrayList<>();
//                            NodeList riderNameNodeList = getNodeList("rider/rider/riderName", latestPlanNode);
//                            //loger.info("riderNameNodeList Size: " + riderNameNodeList.getLength());
//                            if (riderNameNodeList != null) {
//                                for (int k = 0; k < riderNameNodeList.getLength(); k++) {
//                                    riderNameList.add(riderNameNodeList.item(k).getTextContent());
//                                }
//                            }
//
//                            Node effectiveDateNode = oNeilXmlDoc.createElement("effectiveDate");
//                            effectiveDateNode.setTextContent(effectiveDate);
//                            delimitedFileDataMap.put("Effective Date", effectiveDate);
//
//                            Node benefitPlanDescNode = oNeilXmlDoc.createElement("benefitPlanDesc");
//                            benefitPlanDescNode.setTextContent(benefitPlanDesc);
//
//                            delimitedFileDataMap.put("Benefit Plan", benefitPlanDesc);
//                            delimitedFileDataMap.put("Benefit Plan ID", benefitPlanId);
//
//                            // Get Provider Choice Values
//                            List<Node> providerMatchedDateList = new ArrayList<>();
//                            List<Node> providerUnmatchedDateList = new ArrayList<>();
//                            String[] providerNodeNames = {"currentProviderChoices", "pendingProviderChoices", "historicalProviderChoices"};
//                            for (String providerName : providerNodeNames) {
//                                String expression = "correspondenceItem/recipient/member/" + providerName + "/providerChoice/endDate";
//                                NodeList providerNodeList = getNodeList(expression, correspondenceItemListNode);
//                                if (providerNodeList != null)
//                                    filterDates(providerNodeList, providerMatchedDateList, providerUnmatchedDateList, "3000-01-01");
//                            }
//
//                            Node latestProviderNode = getLatestPlanNode(providerMatchedDateList, providerUnmatchedDateList, "3000-01-01", "yyyy-MM-dd");
//                            if (latestProviderNode == null)
//                                throw new Exception("Error: No valid endDates found in currentProviderChoices/pendingProviderChoices/historicalProviderChoices");
//
//                            // Append idCard Node Data here
//                            carrierNode.appendChild(idCardNode);
//                            idCardNode.appendChild(cardNumberNode);
//                            idCardNode.appendChild(cardTemplateNode);
//                            idCardNode.appendChild(memberFullNameNode);
//                            idCardNode.appendChild(memberIdNode);
//                            idCardNode.appendChild(DOBNode);
//                            idCardNode.appendChild(sexNode);
//                            idCardNode.appendChild(effectiveDateNode);
//                            idCardNode.appendChild(benefitPlanDescNode);
//
//                            String choiceType = getNodeText("choiceType", latestProviderNode);
//                            String supplierName;
//                            String pcpPhone = "";
//                            String pcpFullName = "";
//                            String planMedicalGroup = "";
//
//                            if (choiceType.equals("PCP")) {
//
//                                supplierName = getNodeText("supplierName", latestProviderNode);
//
//                                if (supplierName.equalsIgnoreCase("Sharp Rees Stealy Medical Group")
//                                        ||  supplierName.equalsIgnoreCase("Sharp Rees-Stealy Medical Group")) {
//
//                                    pcpPhone = getNodeText("supplierPhone", latestProviderNode);
//                                    pcpFullName = "Sharp Rees-Stealy Medical Group";
//
//                                } else {
//
//                                    pcpPhone = getNodeText("providerPhone", latestProviderNode);
//                                    pcpFullName = getNodeText("firstName", latestProviderNode) + " "
//                                            + getNodeText("lastName", latestProviderNode);
//                                }
//                                planMedicalGroup = getNodeText("affiliatedNetwork", latestProviderNode);
//                            }
//
//                            Node pcpPhoneNode = oNeilXmlDoc.createElement("pcpPhone");
//                            pcpPhoneNode.setTextContent(pcpPhone);
//                            idCardNode.appendChild(pcpPhoneNode);
//
//                            Node pcpFullNameNode = oNeilXmlDoc.createElement("pcpFullName");
//                            pcpFullNameNode.setTextContent(pcpFullName);
//                            idCardNode.appendChild(pcpFullNameNode);
//                            Node planMedicalGroupNode = oNeilXmlDoc.createElement("planMedicalGroup");
//
//                            if (PMGIdAndNameMap.containsKey(planMedicalGroup)) {
//                                planMedicalGroup = PMGIdAndNameMap.get(planMedicalGroup);
//                            }
//                            planMedicalGroupNode.setTextContent(planMedicalGroup);
//                            idCardNode.appendChild(planMedicalGroupNode);
//
//                            Node networkNode = oNeilXmlDoc.createElement("network");
//                            networkNode.setTextContent(network);
//                            idCardNode.appendChild(networkNode);
//
//                            Node tiersNode = oNeilXmlDoc.createElement("tiers");
//                            idCardNode.appendChild(tiersNode);
//
//                            // Create Tier 1 Node
//                            Node tier1Node = oNeilXmlDoc.createElement("tier");
//                            tiersNode.appendChild(tier1Node);
//
//                            Node tierNumberNode = oNeilXmlDoc.createElement("tierNumber");
//                            tierNumberNode.setTextContent("1");
//                            tier1Node.appendChild(tierNumberNode);
//
//                            // Get Tier 1 Values
//                            String xpr = "valueList/valueDefinition/variableName";
//
//                            String deductible = xpr + "[text()='Individual Deductible']";
//                            deductible = getTierValues(deductible, latestPlanNode);
//                            Node deductibleNode = oNeilXmlDoc.createElement("deductible");
//                            deductibleNode.setTextContent(deductible);
//                            tier1Node.appendChild(deductibleNode);
//
//                            String pcpCostShare = xpr + "[text()='PCP Copay' or text()='PCP Coinsurance']";
//                            pcpCostShare = getTierValues(pcpCostShare, latestPlanNode);
//                            Node pcpCostNode = oNeilXmlDoc.createElement("pcpCostShare");
//                            pcpCostNode.setTextContent(pcpCostShare);
//                            tier1Node.appendChild(pcpCostNode);
//
//                            String specialistCostShare = xpr + "[text()='Specialist Copay' or text()='Specialist Coinsurance']";
//                            specialistCostShare = getTierValues(specialistCostShare, latestPlanNode);
//                            Node specialistCostNode = oNeilXmlDoc.createElement("specialistCostShare");
//                            specialistCostNode.setTextContent(specialistCostShare);
//                            tier1Node.appendChild(specialistCostNode);
//
//                            String hospitalCostShare = xpr + "[text()='Hospital Copay' or text()='Hospital Coinsurance']";
//                            hospitalCostShare = getTierValues(hospitalCostShare, latestPlanNode);
//                            Node hospitalCostNode = oNeilXmlDoc.createElement("hospitalCostShare");
//                            hospitalCostNode.setTextContent(hospitalCostShare);
//                            tier1Node.appendChild(hospitalCostNode);
//
//                            String urgentCareCostShare = xpr + "[text()='Urgent Care Copay' or text()='Urgent Care Coinsurance']";
//                            urgentCareCostShare = getTierValues(urgentCareCostShare, latestPlanNode);
//                            Node urgentCareCostNode = oNeilXmlDoc.createElement("urgentCareCostShare");
//                            urgentCareCostNode.setTextContent(urgentCareCostShare);
//                            tier1Node.appendChild(urgentCareCostNode);
//
//                            String erCostShare = xpr + "[text()='ER Copay' or text()='ER Coinsurance']";
//                            erCostShare = getTierValues(erCostShare, latestPlanNode);
//                            Node erCostShareNode = oNeilXmlDoc.createElement("erCostShare");
//                            erCostShareNode.setTextContent(erCostShare);
//                            tier1Node.appendChild(erCostShareNode);
//
//                            // Get Tier 2 Values if POS Small or Large
//                            if (product.equals("POS Small") || product.equals("POS Large")) {
//
//                                System.out.println("--- Tier 2 Values ---");
//
//                                Node tier2Node = oNeilXmlDoc.createElement("tier");
//                                tiersNode.appendChild(tier2Node);
//
//                                Node tierNumberNode2 = oNeilXmlDoc.createElement("tierNumber");
//                                tierNumberNode2.setTextContent("2");
//                                tier2Node.appendChild(tierNumberNode2);
//
//                                // Get Tier 2 Deductible
//                                String deductible2 = xpr + "[text()='Individual OON Deductible']";
//                                deductible2 = getTierValues(deductible2, latestPlanNode);
//
//                                // Get Tier 2 Coinsurance
//                                String coinsurance = xpr + "[text()='Tier 2 Coinsurance']";
//                                coinsurance = getTierValues(coinsurance, latestPlanNode);
//
//                                // Set Tier 2 Deductible Node
//                                Node deductibleNode2 = oNeilXmlDoc.createElement("deductible");
//                                if (deductible2.equals("")) {
//                                    deductibleNode2.setTextContent(coinsurance);
//                                } else {
//                                    deductibleNode2.setTextContent(deductible2);
//                                }
//                                tier2Node.appendChild(deductibleNode2);
//                                System.out.println("Tier 2 Deductible: " + deductible2);
//
//                                // Set Tier 2 pcpCostShare Node
//                                Node pcpCostShareNode2 = oNeilXmlDoc.createElement("pcpCostShare");
//                                pcpCostShareNode2.setTextContent(coinsurance);
//                                tier2Node.appendChild(pcpCostShareNode2);
//                                System.out.println("Tier 2 PCP Copay/Coinsurance: " + coinsurance);
//
//                                // Set Tier 2 specialistCostShare Node
//                                Node specialistCostShareNode2 = oNeilXmlDoc.createElement("specialistCostShare");
//                                specialistCostShareNode2.setTextContent(coinsurance);
//                                tier2Node.appendChild(specialistCostShareNode2);
//                                System.out.println("Tier 2 Specialist Copay/Coinsurance: " + coinsurance);
//
//                                // Set Tier 2 specialistCostShare Node
//                                Node hospitalCostShareNode2 = oNeilXmlDoc.createElement("hospitalCostShare");
//                                hospitalCostShareNode2.setTextContent(coinsurance);
//                                tier2Node.appendChild(hospitalCostShareNode2);
//                                System.out.println("Tier 2 Hospital Copay/Coinsurance: " + coinsurance);
//
//                                // Set Tier 2 specialistCostShare Node
//                                Node urgentCareCostShareNode2 = oNeilXmlDoc.createElement("urgentCareCostShare");
//
//                                System.out.println("benefitPlanDesc : " + benefitPlanDesc);
//                                if (benefitPlanDesc.equalsIgnoreCase("POS NG 1 L")
//                                        || benefitPlanDesc.equalsIgnoreCase("Palomar Health POS NG 1 L")) {
//                                    urgentCareCostShareNode2.setTextContent(urgentCareCostShare);
//                                    System.out.println("Tier 2 Urgent Care Copay/Coinsurance: " + urgentCareCostShare);
//                                } else {
//                                    urgentCareCostShareNode2.setTextContent(coinsurance);
//                                    System.out.println("Tier 2 Urgent Care Copay/Coinsurance: " + coinsurance);
//                                }
//                                tier2Node.appendChild(urgentCareCostShareNode2);
//
//                                // Set Tier 2 erCostShare Node
//                                Node erCostShareNode2 = oNeilXmlDoc.createElement("erCostShare");
//                                erCostShareNode2.setTextContent(erCostShare);
//                                tier2Node.appendChild(erCostShareNode2);
//                                System.out.println("Tier 2 ER Copay/Coinsurance: " + erCostShare);
//                            }
//
//                            Node returnImageFileNameNode = oNeilXmlDoc.createElement("returnImageFileName");
//                            returnImageFileNameNode.setTextContent("");
//                            idCardNode.appendChild(returnImageFileNameNode);
//
//                            Node trackingValuesNode = oNeilXmlDoc.createElement("trackingValues");
//                            idCardNode.appendChild(trackingValuesNode);
//
//                            String documentTypeXpath = "correspondenceItem/documentType";
//                            documentType = getNodeText(documentTypeXpath, correspondenceItemListNode);
//
//                            Node documentTypeNode = oNeilXmlDoc.createElement("documentType");
//                            documentTypeNode.setTextContent(documentType);
//                            trackingValuesNode.appendChild(documentTypeNode);
//                            definitionSet.add(documentType);
//
//                            if (memberExistenceCheckFlag.equalsIgnoreCase("On")) {
//                                MemberExistenceCheck objMemberExistenceCheck = new MemberExistenceCheck(memberId);
//                                initialCardRunSet.add(objMemberExistenceCheck.getIntialCardRunValue());
//                            }
//
//                            String correspondenceIdXpath = "correspondenceItem/correspondenceId";
//                            String correspondenceId = getNodeText(correspondenceIdXpath, correspondenceItemListNode);
//
//                            Node idNode = oNeilXmlDoc.createElement("id");
//                            idNode.setTextContent(correspondenceId);
//                            trackingValuesNode.appendChild(idNode);
//
//                            String accountNameXpath = "correspondenceItem/recipient/member/account/name";
//                            String accountName = getNodeText(accountNameXpath, correspondenceItemListNode);
//
//                            Node accountNameNode = oNeilXmlDoc.createElement("accountName");
//                            accountNameNode.setTextContent(accountName);
//                            trackingValuesNode.appendChild(accountNameNode);
//
//                            String accountIdXpath = "correspondenceItem/recipient/member/account/id";
//                            String accountId = getNodeText(accountIdXpath, correspondenceItemListNode);
//
//                            Node accountIdNode = oNeilXmlDoc.createElement("accountId");
//                            accountIdNode.setTextContent(accountId);
//                            trackingValuesNode.appendChild(accountIdNode);
//
//                            String isAccountCalPERS = (topAccountName.equals("CalPERS")) ? "Yes" : "No";
//                            String intialCardRunValue = "";
//                            if (memberExistenceCheckFlag.equalsIgnoreCase("Off")) {
//                                intialCardRunValue = "Brand New Member"; // This needs to be find out based on Sharp table
//                            } else if (memberExistenceCheckFlag.equalsIgnoreCase("On")) {
//                                if (initialCardRunSet.size() > 1) {
//                                    intialCardRunValue = "Brand New Member";
//                                } else {
//                                    for (String anInitialCardRunSet : initialCardRunSet) {
//                                        intialCardRunValue = anInitialCardRunSet;
//                                    }
//                                }
//                            }
//
//                            //loger.info("intialCardRunValue = " + intialCardRunValue);
//                            //loger.info("definitionSet size: " + definitionSet.size());
//
//                            String idCardLetterID = getIDCardLetterID(documentType, intialCardRunValue, isAccountCalPERS, props);
//                            letterIDNode = oNeilXmlDoc.createElement("letterID");
//                            letterIDNode.setTextContent(idCardLetterID);
//
//                            delimitedFileDataMap.put("Letter #", idCardLetterID);
//
//                            String visionRider = "";
//                            String mentalHealthRider = "";
//                            String pharmacyRider = "";
//                            String chiroAcupunctureRider = "";
//                            String isDentalRiderExist = "No";
//
//                            for (String riderName : riderNameList) {
//                                //loger.info("ridername: " + riderName);
//                                if (visionRider.equals("") && visionRiderList.contains(riderName)) {
//                                    visionRider = riderName;
//                                }
//                                if (mentalHealthRider.equals("") && mentalHealthRiderList.contains(riderName)) {
//                                    mentalHealthRider = riderName;
//                                }
//                                if (pharmacyRider.equals("") && pharmacyRiderList.contains(riderName)) {
//                                    pharmacyRider = riderName;
//                                }
//                                if (chiroAcupunctureRider.equals("") && chiroAcupunctureRiderList.contains(riderName)) {
//                                    chiroAcupunctureRider = riderName;
//                                }
//                                if (riderName.equals("Dental")) {
//                                    isDentalRiderExist = "Yes";
//                                }
//                            }
//
//                            Node visionRiderNode = oNeilXmlDoc.createElement("visionRider");
//                            visionRiderNode.setTextContent(visionRider);
//                            trackingValuesNode.appendChild(visionRiderNode);
//
//                            delimitedFileDataMap.put("Vision Plan Rider", visionRider);
//
//                            Node mentalHealthRiderNode = oNeilXmlDoc.createElement("mentalHealthRider");
//                            mentalHealthRiderNode.setTextContent(mentalHealthRider);
//                            trackingValuesNode.appendChild(mentalHealthRiderNode);
//
//                            delimitedFileDataMap.put("Mental Health Rider", mentalHealthRider);
//
//                            Node pharmacyRiderNode = oNeilXmlDoc.createElement("pharmacyRider");
//                            pharmacyRiderNode.setTextContent(pharmacyRider);
//                            trackingValuesNode.appendChild(pharmacyRiderNode);
//
//                            delimitedFileDataMap.put("Pharmacy Rider", pharmacyRider);
//
//                            Node chiroAcupunctureRiderNode = oNeilXmlDoc.createElement("chiroAcupunctureRider");
//                            chiroAcupunctureRiderNode.setTextContent(chiroAcupunctureRider);
//                            trackingValuesNode.appendChild(chiroAcupunctureRiderNode);
//
//                            delimitedFileDataMap.put("Chiro/Acupuncture Rider", chiroAcupunctureRider);
//
//                            // New ID Card Template selection process
//
//                            // Get topAccountId from hierarchialAccountInformation Node
//                            String topAccountIdXpath = "correspondenceItem/recipient/member/hierarchialAccountInformation/topAccountId";
//                            String topAccountId = getNodeText(topAccountIdXpath, correspondenceItemListNode);
//                            String idCardTemplate = "";
//
//                            //loger.info("Choosing Template ID");
//                            //loger.info("Product Name: " + product);
//                            //loger.info("Top Account ID: " + topAccountId);
//                            //loger.info("Top Account Name: " + topAccountName);
//                            //loger.info("Dental Rider Exist: " + isDentalRiderExist);
//                            if (product.startsWith("Covered California")) {
//                                idCardTemplate = "On-Exchange";
//                            } else if (product.startsWith("POS") && topAccountId.equals("1002141")) {
//                                idCardTemplate = "Custom Client POS";
//                            } else if (topAccountId.equals("1002141")) {
//                                idCardTemplate = "Custom Client";
//                            } else if (product.startsWith("POS")) {
//                                idCardTemplate = "POS";
//                            } else if (topAccountId.equals("1006268")) {
//                                idCardTemplate = "MEA";
//                            } else if (topAccountId.equals("1002026")) {
//                                idCardTemplate = "CalPERS";
////							} else if (isDentalRiderExist.equals("Yes")) {
//                            } else if (isDentalRiderExist.equals("Yes") ||  topAccountId.equals("1008002") || topAccountId.equals("1009001")) {
//                                idCardTemplate = "Off-Exchange (Access Dental)";
//                            } else {
//                                idCardTemplate = "Off-Exchange";
//                            }
//
//
////							//loger.info(String.format("Before calling idCardTemplate: product=%s, topAccountName=%s, isDentalRiderExist=%s",
////									product, topAccountName, isDentalRiderExist));
////							String idCardTemplate = getIDCardTemplate(product, topAccountName, isDentalRiderExist, props);
//                            cardTemplateNode.setTextContent(idCardTemplate);
//
//                            delimitedFileDataMap.put("ID Card Template", idCardTemplate);
//
//                            //loger.info("before adding map to list");
//
//                            delimitedFileDataList.add(delimitedFileDataMap);
//
//
//
//                        } catch (Exception e) {
//                            //loger.error("Exception while processing the id cards.");
//                            e.printStackTrace();
//                            throw new Exception(e);
//                        }
//
//                        processedFilesCount++;
//                        processedFilesList.add(f.getName());
//
//                        // Create Updater Config XML
//                        try {
//
//                            // correspondence
//                            Node correspondenceNode = updaterConfigXmlDoc.createElement("correspondence");
//                            updaterConfigRootNode_correspondences.appendChild(correspondenceNode);
//
//                            // Recipient
//                            Element RecipientEle = updaterConfigXmlDoc.createElement("recipient");
//                            String subscriberID = "";
//                            try {
//                                subscriberID = idCardInputXmlDoc.getElementsByTagName("subscriberId").item(0).getTextContent();
//                            } catch (Exception ignore) {
//                            }
//
//                            Node recipientTypeNode = updaterConfigXmlDoc.createElement("type");
//                            recipientTypeNode.setTextContent("Membership");
//                            RecipientEle.appendChild(recipientTypeNode);
//
//                            Node recipientHccIdNode = updaterConfigXmlDoc.createElement("hccId");
//                            recipientHccIdNode.setTextContent(subscriberID);
//                            RecipientEle.appendChild(recipientHccIdNode);
//                            correspondenceNode.appendChild(RecipientEle);
//
//                            // id
//                            Node idNode = updaterConfigXmlDoc.createElement("id");
//                            idNode.setTextContent(idCardInputXmlDoc.getElementsByTagName("correspondenceId").item(0).getTextContent());
//                            correspondenceNode.appendChild(idNode);
//
//                            // status
//                            Node statusNode = updaterConfigXmlDoc.createElement("status");
//                            statusNode.setTextContent("p");
//                            correspondenceNode.appendChild(statusNode);
//
//                            // Description
//                            Node DescriptionNode = updaterConfigXmlDoc.createElement("description");
//                            DescriptionNode.setTextContent(idCardInputXmlDoc.getElementsByTagName("documentType").item(0).getTextContent());
//                            correspondenceNode.appendChild(DescriptionNode);
//
//                            // Definition
//                            Element DefinitionEle = updaterConfigXmlDoc.createElement("definition");
//                            String definition = "";
//                            try {
//                                definition = idCardInputXmlDoc.getElementsByTagName("documentType").item(0).getTextContent();
//                            } catch (Exception ignore) {
//                            }
//
//                            Node descNameNode = updaterConfigXmlDoc.createElement("name");
//                            descNameNode.setTextContent(definition);
//                            DefinitionEle.appendChild(descNameNode);
//                            correspondenceNode.appendChild(DefinitionEle);
//
//                            // Subject
//                            Element SubjectEle = updaterConfigXmlDoc.createElement("subject");
//
//                            Node subjectTypeNode = updaterConfigXmlDoc.createElement("type");
//                            subjectTypeNode.setTextContent("Membership");
//                            SubjectEle.appendChild(subjectTypeNode);
//
//                            Node subjectHccIdNode = updaterConfigXmlDoc.createElement("hccId");
//                            subjectHccIdNode.setTextContent(subscriberID);
//                            SubjectEle.appendChild(subjectHccIdNode);
//                            correspondenceNode.appendChild(SubjectEle);
//
//                        } catch (Exception e) {
//                            //loger.error("Exception while creating updater config/response XML.");
//                            e.printStackTrace();
//                        }
//                        // End: Create updater Config xml;
//                    } catch (Exception e) {
//                        //loger.error("Exception occurred while processing file: " + f.getName(), e);
//                        //loger.info("Adding '" + f.getName() + "' to filesWithIssues List!" );
//                        filesWithIssues.add(f.getName());
//                        errorFileDesc.append(f.getName()).append(" ::: ").append("not able to parse the xml file. XML format may be incorrect.");
//                        if (e.getMessage() != null && !"".equals(e.getMessage())) {
//                            errorFileDesc.append("Error is ").append(e.getMessage());
//                        }
//                        errorFileDesc.append(newLine);
//                        continue;
//                    }
//
//                    oNeilRootNode_memberIdCards.appendChild(envelopeNode);
//                    envelopeNode.appendChild(carrierNode);
//
//                    if (envelopeCount == 0)
//                        envelopeNode.insertBefore(letterIDNode, carrierNode);
//
////					// Check if Effective Date is after 1-1-2020
////					Date effDate = new SimpleDateFormat("yyyy-MM-dd").parse(effectiveDate);
////					Date compareDate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-01");
////					//loger.info("effDate = " + effDate.toString() + "; compareDate = " + compareDate.toString());
////					if (effDate.before(compareDate)) {
////						//loger.info("eff date is before compare");
////						envelopeCount++;
////					} else {
////						//loger.info("eff date is after compare");
////						envelopeCount = 2;
////					}
//                    envelopeCount++;
//
//
//                    //loger.info("envelopeCount = " + envelopeCount);
//
//
//                    if (envelopeCount == 2) {
//                        carrierNode = oNeilXmlDoc.createElement("carrier");
//                        envelopeNode = oNeilXmlDoc.createElement("envelope");
//                        envelopeCount = 0;
//
//                        String isAccountCalPERS = "No";
//                        if (topAccountName.equals("CalPERS")) {
//                            isAccountCalPERS = "Yes";
//                        }
//                        String initialCardRunValue = "";
//                        if (memberExistenceCheckFlag.equalsIgnoreCase("Off")) {
//                            initialCardRunValue = "Brand New Member";
//                        } else if (memberExistenceCheckFlag.equalsIgnoreCase("On")) {
//                            if (initialCardRunSet.size() > 1) {
//                                initialCardRunValue = initialCardRunSet.iterator().next();
//                                documentType = definitionSet.iterator().next();
//                            } else {
//                                for (String anInitialCardRunSet : initialCardRunSet) {
//                                    initialCardRunValue = anInitialCardRunSet;
//                                }
//                            }
//                        }
//                        //loger.info("definitionSet size: " + definitionSet.size());
//                        // If one envelope contains members that do not have the same letter type then the New Member letter type will be used
//                        if (definitionSet.size() > 1) {
//                            for (String definitionName : definitionSet) {
//                                ////loger.info("definitionName : " + definitionName);
//                            }
//                            documentType = definitionSet.iterator().next();
//
//                            if (memberExistenceCheckFlag.equalsIgnoreCase("Off")) {
//                                initialCardRunValue = "Brand New Member";
//                            } else {
//                                initialCardRunValue = initialCardRunSet.iterator().next();
//                            }
//                        }
//                        String idCardLetterID = getIDCardLetterID(documentType, initialCardRunValue, isAccountCalPERS, props);
//                        letterIDNode.setTextContent(idCardLetterID);
//                        delimitedFileDataMap.put("Letter #", idCardLetterID);
//                    }
//                }
//                NodeList carrierNodeList = oNeilXmlDoc.getElementsByTagName("carrier");
//                totalCarriersNode.setTextContent("" + carrierNodeList.getLength());
//            }
//        }
//        //loger.info("cancelledExtractsMap size: " + cancelledExtractsMap.size());
//        if (cancelledExtractsMap.size() > 0) {
//            Iterator it = cancelledExtractsMap.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry<Object, Object> pair = (Map.Entry<Object, Object>) it.next();
//                String id = (String) pair.getKey();
//                String subScriberId = (String) pair.getValue();
//                // correspondence
//                Node correspondenceNode = updaterConfigXmlDoc.createElement("correspondence");
//                updaterConfigRootNode_correspondences.appendChild(correspondenceNode);
//                // Recipient
//                Element RecipientEle = updaterConfigXmlDoc.createElement("recipient");
//                Node recipientTypeNode = updaterConfigXmlDoc.createElement("type");
//                recipientTypeNode.setTextContent("Membership");
//                RecipientEle.appendChild(recipientTypeNode);
//                Node recipientHccIdNode = updaterConfigXmlDoc.createElement("hccId");
//                recipientHccIdNode.setTextContent(subScriberId);
//                RecipientEle.appendChild(recipientHccIdNode);
//                correspondenceNode.appendChild(RecipientEle);
//                // id
//                Node idNode = updaterConfigXmlDoc.createElement("id");
//                idNode.setTextContent(id);
//                correspondenceNode.appendChild(idNode);
//                // status
//                Node statusNode = updaterConfigXmlDoc.createElement("status");
//                statusNode.setTextContent("c");
//                correspondenceNode.appendChild(statusNode);
//                // Description
//                Node DescriptionNode = updaterConfigXmlDoc.createElement("description");
//                DescriptionNode.setTextContent("Duplicate Member ID Request");
//                correspondenceNode.appendChild(DescriptionNode);
//                // Definition
//                Element DefinitionEle = updaterConfigXmlDoc.createElement("definition");
//                Node descNameNode = updaterConfigXmlDoc.createElement("name");
//                descNameNode.setTextContent(deDupDocType);
//                DefinitionEle.appendChild(descNameNode);
//                correspondenceNode.appendChild(DefinitionEle);
//                // Subject
//                Element SubjectEle = updaterConfigXmlDoc.createElement("subject");
//                Node subjectTypeNode = updaterConfigXmlDoc.createElement("type");
//                subjectTypeNode.setTextContent("Membership");
//                SubjectEle.appendChild(subjectTypeNode);
//                Node subjectHccIdNode = updaterConfigXmlDoc.createElement("hccId");
//                subjectHccIdNode.setTextContent(subScriberId);
//                SubjectEle.appendChild(subjectHccIdNode);
//                correspondenceNode.appendChild(SubjectEle);
//
//            }
//        }
//        //String updaterConfigXml = Utility.getXMLStringFromNodeNoXMLVersion(updaterConfigXmlDoc);
//        String outputDir = props.getProperty(currProcess + ".outputDirectoryPath");
//        String outputFolder = props.getProperty(currProcess + ".outputFolderName");
//        String outputFilePath = outputDir + fileSeparator + outputFolder;
//        idcardsUpdaterConfigResponseFileName = "Response_IDCards_UpdaterConfig_" + filenameTimeStamp + ".xml";
//        //Utility.createFile(updaterConfigXml, outputFilePath + fileSeparator + idcardsUpdaterConfigResponseFileName);
//        saveNodeToFile(updaterConfigXmlDoc, new File(outputFilePath + fileSeparator + idcardsUpdaterConfigResponseFileName));
//
//        //result.append(Utility.getXMLStringFromNodeNoXMLVersion(oNeilXmlDoc));
//        saveNodeToFile(oNeilXmlDoc, new File(outputFilePath + fileSeparator + idcardsOutputFileName));
//
//        StringBuffer delimitedReportFileContent = getDelimitedReportFileContent(delimitedFileDataList);
//        createFile(delimitedReportFileContent.toString(), outputFilePath + fileSeparator + "mkr_" + filenameTimeStamp + ".txt");
//    }
//    private String getTierValues(String expression, Node latestPlanNode) throws XPathExpressionException {
//        Node child = getNode(expression, latestPlanNode);
//        String returnValue = "$0";
//        if (child != null) {
//            if (child.getPreviousSibling().getNodeName().equals("currencyValue"))
//                returnValue = child.getPreviousSibling().getTextContent();
//            if (child.getPreviousSibling().getNodeName().equals("percentValue"))
//                returnValue = String.format("%d%%", 100 - Integer.parseInt(child.getPreviousSibling().getTextContent()));
//        }
//        return returnValue;
//    }
//    private StringBuffer getDelimitedReportFileContent(List<Map> delimitedFileDataList) {
//        StringBuffer reportFileContent = null;
//        try {
//            reportFileContent = new StringBuffer();
//            reportFileContent.append("ID Card Template|Product|Employer Group Name|Member ID|Member Name|")
//                    .append("Member Birth Date|Effective Date|Correspondence Address|Correspondence City|Correspondence State|Correspondence Zip|")
//                    .append("Benefit Plan|Vision Plan Rider|Mental Health Rider|Pharmacy Rider|Chiro/Acupuncture Rider|Letter #|Benefit Plan ID")
//                    .append(newLine);
//            for (Map aDelimitedFileDataList : delimitedFileDataList) {
//                Map<String, String> delimitedFileDataMap = aDelimitedFileDataList;
//                String iDCardTemplate = delimitedFileDataMap.get("ID Card Template");
//                String product = delimitedFileDataMap.get("Product");
//                String employerGroupName = delimitedFileDataMap.get("Employer Group Name");
//                String memberID = delimitedFileDataMap.get("Member ID");
//                String memberName = delimitedFileDataMap.get("Member Name");
//                String memberBirthDate = delimitedFileDataMap.get("Member Birth Date");
//                String effectiveDate = delimitedFileDataMap.get("Effective Date");
//                String correspondenceAddress = delimitedFileDataMap.get("Correspondence Address");
//                String correspondenceCity = delimitedFileDataMap.get("Correspondence City");
//                String correspondenceState = delimitedFileDataMap.get("Correspondence State");
//                String correspondenceZip = delimitedFileDataMap.get("Correspondence Zip");
//                String benefitPlan = delimitedFileDataMap.get("Benefit Plan");
//                String visionPlanRider = delimitedFileDataMap.get("Vision Plan Rider");
//                String mentalHealthRider = delimitedFileDataMap.get("Mental Health Rider");
//                String pharmacyRider = delimitedFileDataMap.get("Pharmacy Rider");
//                String chiroAcupunctureRider = delimitedFileDataMap.get("Chiro/Acupuncture Rider");
//                String letterID = delimitedFileDataMap.get("Letter #");
//                String isSubscriber = delimitedFileDataMap.get("isSubscriber");
//                String benefitPlanId = delimitedFileDataMap.get("Benefit Plan ID");
//                if (isSubscriber.equalsIgnoreCase("true")) {
//                    reportFileContent.append((iDCardTemplate == null ? "" : iDCardTemplate) + "|");
//                    reportFileContent.append((product == null ? "" : product) + "|");
//                    reportFileContent.append((employerGroupName == null ? "" : employerGroupName) + "|");
//                    reportFileContent.append((memberID == null ? "" : memberID) + "|");
//                    reportFileContent.append((memberName == null ? "" : memberName) + "|");
//                    reportFileContent.append((memberBirthDate == null ? "" : memberBirthDate) + "|");
//                    reportFileContent.append((effectiveDate == null ? "" : effectiveDate) + "|");
//                    reportFileContent.append((correspondenceAddress == null ? "" : correspondenceAddress) + "|");
//                    reportFileContent.append((correspondenceCity == null ? "" : correspondenceCity) + "|");
//                    reportFileContent.append((correspondenceState == null ? "" : correspondenceState) + "|");
//                    reportFileContent.append((correspondenceZip == null ? "" : correspondenceZip) + "|");
//                    reportFileContent.append((benefitPlan == null ? "" : benefitPlan) + "|");
//                    reportFileContent.append((visionPlanRider == null ? "" : visionPlanRider) + "|");
//                    reportFileContent.append((mentalHealthRider == null ? "" : mentalHealthRider) + "|");
//                    reportFileContent.append((pharmacyRider == null ? "" : pharmacyRider) + "|");
//                    reportFileContent.append((chiroAcupunctureRider == null ? "" : chiroAcupunctureRider) + "|");
//                    reportFileContent.append(letterID == null ? "" : letterID + "|");
//                    reportFileContent.append(benefitPlanId == null ? "" : benefitPlanId);
//                    reportFileContent.append(newLine);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            //loger.error("Exception while creating delimited report file : ", e);
//        }
//        return reportFileContent;
//    }
//    private String getIDCardLetterID(String corrDefinitionValue, String initialCardRunValue, String isAccountCalPERS, Properties props) {
//        //loger.info("corrDefinitionValue = " + corrDefinitionValue);
//        //loger.info("initialCardRunValue = " + initialCardRunValue);
//        //loger.info("isAccountCalPERS = " + isAccountCalPERS);
//        String idCardLetterID = "";
//        Map<String, String> idCardLetterIDMap;
//        try {
//            idCardLetterIDMap = readLetterIDProps(props);
//            Iterator it = idCardLetterIDMap.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry<Object, Object> pair = (Map.Entry<Object, Object>) it.next();
//                String key = (String) pair.getKey();
//                String mapKey = "";
//                String corrDefinition = "";
//                corrDefinition = corrDefinitionValue;
//                String intialCardRun = "";
//                String isAccountCalPERSExists = "";
//                String[] keySplitArray = key.split(",");
//                String corrDefinitionTemp = keySplitArray[0];
//                String intialCardRunTemp = keySplitArray[1];
//                String isAccountCalPERSTemp = keySplitArray[2];
//                ////loger.info("Map Values");
//                ////loger.info("corrDefinitionTemp = " + corrDefinitionTemp);
//                ////loger.info("intialCardRunTemp = " + intialCardRunTemp);
//                ////loger.info("isAccountCalPERSTemp = " + isAccountCalPERSTemp);
//                if (corrDefinitionTemp.equals(corrDefinition)) {
//                    if (intialCardRunTemp.equals("NA")) {
//                        intialCardRun = "NA";
//                    } else {
//                        if (intialCardRunTemp.equals(initialCardRunValue)) {
//                            intialCardRun = initialCardRunValue;
//                        } else {
//                            intialCardRun = initialCardRunValue;
//                        }
//                    }
//                    if (isAccountCalPERSTemp.equals("NA")) {
//                        isAccountCalPERSExists = isAccountCalPERSTemp;
//                    } else {
//                        isAccountCalPERSExists = isAccountCalPERS;
//                    }
//                } else {
//                    continue;
//                }
//                mapKey = corrDefinition + "," + intialCardRun + "," + isAccountCalPERSExists;
//                ////loger.info("mapKey Letter ID: " + mapKey);
//                idCardLetterID = idCardLetterIDMap.get(mapKey);
//                if (idCardLetterID != null && !idCardLetterID.equals("")) {
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            //loger.info("Ignore Exception while reading IDCardTemplate.");
//        }
//        return idCardLetterID;
//    }
//    private String getIDCardTemplate(String productValue, String accountValue, String riderExistsValue, Properties props) {
//        String idCardTemplate = "";
//        Map<String, String> idCardTemplateMap;
//        try {
//            idCardTemplateMap = readIDCardTemplateProps(props);
//            Iterator it = idCardTemplateMap.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry<Object, Object> pair = (Map.Entry<Object, Object>) it.next();
//                String key = (String) pair.getKey();
//                String mapKey = "";
//                String product = "";
//                product = productValue;
//                String account = "";
//                String riderExists = "";
//                String[] keySplitArray = key.split(",");
//                String productTemp = keySplitArray[0];
//                String accountTemp = keySplitArray[1];
//                String riderExistsTemp = keySplitArray[2];
//                //loger.info("Working on key: " + key);
//                //loger.info("productTemp: " + productTemp + " vs product: " + product);
//                if (productTemp.equals(product)) {
//                    //loger.info("accountTemp: " + accountTemp + " vs accountValue: " + accountValue);
//                    if (accountTemp.equals("NA")) {
//                        account = "NA";
//                    } else {
//                        if (accountTemp.startsWith("!")) {
//                            accountTemp = accountTemp.substring(1);
//                            if (accountTemp.equals(accountValue)) {
//                                account = accountValue;
//                            } else {
//                                account = "!" + accountTemp;
//                            }
//                        } else if (!accountTemp.startsWith("!")) {
//                            if (accountTemp.equals(accountValue)) {
//                                account = accountValue;
//                            } else {
//                                account = accountValue;
//                            }
//                        }
//                    }
//                    //loger.info("account: " + account);
//                    //loger.info("riderExistsTemp: " + riderExistsTemp + " vs riderExistsValue: " + riderExistsValue);
//                    if (riderExistsTemp.equals("NA")) {
//                        riderExists = riderExistsTemp;
//                    } else {
//                        riderExists = riderExistsValue;
//                    }
//                    //loger.info("riderExists: " + riderExists);
//                } else {
//                    continue;
//                }
//                mapKey = product + "," + account + "," + riderExists;
//                //loger.info("product,account,riderExists: " + mapKey );
//                ////loger.info("mapKey : " + mapKey);
//                idCardTemplate = idCardTemplateMap.get(mapKey);
//                //loger.info("Template ID = " + idCardTemplate);
//                if (idCardTemplate != null && !idCardTemplate.equals("")) {
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            //loger.info("Ignore Exception while reading IDCardTemplate.");
//        }
//        return idCardTemplate;
//    }
//    private List<String> getRiderList(Properties props, String riderName) {
//        List<String> riderList = null;
//        //loger.info("Reading Rider: " + riderName);
//        System.out.println("Reading Rider: " + riderName);
//        try {
//            riderList = new ArrayList<>();
//            String propValue = props.getProperty(riderName);
//            String[] riderValues = propValue.split(",");
//            riderList = Arrays.asList(riderValues);
//        } catch (Exception e) {
//            //loger.error("Exception while reading rider list from config.properties : ", e);
//            e.printStackTrace();
//        }
//        return riderList;
//    }
//    private Map<String, String> readIDCardTemplateProps(Properties props) {
//        Map<String, String> idCardTemplateMap = null;
//        try {
//            idCardTemplateMap = new LinkedHashMap<>();
//            Set<Object> propsSet = props.keySet();
//            for (Object k : propsSet) {
//                String propertyName = (String) k;
//                if (propertyName.startsWith("idcardtemplate")) {
//                    String propertyValue = props.getProperty(propertyName);
//                    String[] propNameSplitArr = propertyValue.split("\\|");
//                    idCardTemplateMap.put(propNameSplitArr[0], propNameSplitArr[1]);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            //loger.error("Exception while reading idcardtemplates properties.");
//        }
//        return idCardTemplateMap;
//    }
//    private Map<String, String> readLetterIDProps(Properties props) {
//        Map<String, String> idCardTemplateMap = null;
//        try {
//            idCardTemplateMap = new HashMap<>();
//            Set<Object> propsSet = props.keySet();
//            for (Object k : propsSet) {
//                String propertyName = (String) k;
//                if (propertyName.startsWith("idcardletterid")) {
//                    String propertyValue = props.getProperty(propertyName);
//                    String[] propNameSplitArr = propertyValue.split("\\|");
//                    idCardTemplateMap.put(propNameSplitArr[0], propNameSplitArr[1]);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            //loger.error("Exception while reading idcard letterid properties.");
//        }
//        return idCardTemplateMap;
//    }
//
//
//}
//
