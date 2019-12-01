package org.sharp.dig

import org.apache.log4j.Logger
import org.dom4j.*
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class IdCards {


    val chiropractorAcupunctureRider = listOf(
        "AC10_12",
        "AC10_15",
        "AC10_20",
        "AC15_12",
        "AC15_15",
        "AC15_20",
        "ACCH10_12",
        "ACCH10_15",
        "ACCH10_20",
        "ACCH10_40",
        "ACCH15_12",
        "ACCH15_15",
        "ACCH15_20",
        "ACCH15_40",
        "ACCH5_40",
        "CH5_40",
        "CHB",
        "CHD"
    )
    val mentalHealthRider = listOf(
        "MHAC",
        "MHDT",
        "MHE",
        "MHFL",
        "MHFM",
        "MHFO",
        "MHG",
        "MHGTE",
        "MHGTF",
        "MHGTG",
        "MHGTH",
        "MHGTI",
        "MHGTJ",
        "MHGTK",
        "MHGTL",
        "MHGTM",
        "MHGTN",
        "MHGTO",
        "MHGTP",
        "MHGTQ",
        "MHGTR",
        "MHGTS",
        "MHGTT",
        "MHGTU",
        "MHGTV",
        "MHGUA",
        "MHH",
        "MHLE",
        "MHLF",
        "MHLL",
        "MHLS",
        "MHLSE",
        "MHLT",
        "MHLTH",
        "MHMA",
        "MHPA",
        "MHPB",
        "MHPC",
        "MHPD",
        "MHPE",
        "MHPF",
        "MHPZ",
        "MHSC",
        "MHWT",
        "MHWU",
        "MHWX",
        "MHXS",
        "MHXT",
        "MHXV",
        "MHXW",
        "MHXX",
        "OMHA",
        "OMHAB",
        "OMHB",
        "OMHC"
    )
    val pharmacyRider = listOf(
        "RX10_20_40",
        "RX10_25_35",
        "RX10_25_50",
        "RX10_25_50_150DED",
        "RX10_25_50_250DED",
        "RX10_35_250DED",
        "RX15_25_50",
        "RX15_30_50",
        "RX15_35_50",
        "RX15_35_50_150DED",
        "RX15_35_50_250DED",
        "RX20_30_60_200DED",
        "RX20_35_70",
        "RX20_35_70_150DED",
        "RX20_35_70_250DED_CalYr",
        "RX20_35_70_250DED_PlanYr",
        "RX5_15_30"
    )
    val pmgIdNameMap = mapOf(
        "GTC" to "Greater Tri-Cities IPA",
        "IND" to "Independent",
        "IND - Encompass" to "Independent",
        "PCAMG" to "Primary Care Associates Medical Group",
        "RCHN" to "Rady Children's Health Network/CPMG",
        "SCMG" to "Sharp Community Medical Group",
        "SCMG-Arch" to "SCMG Arch Health Medical Group",
        "SCMG-Graybill" to "SCMG Graybill",
        "SCMG-Inland" to "SCMG Inland North",
        "SRS" to "Sharp Rees-Stealy Medical Group"
    )
    val visionRider = listOf("VSA0", "VSA2", "VSA2B", "VSA5", "VSA8", "VSOE")


    fun processIdCards(inputPath: String) {
        logger = Logger.getLogger("processIdCards")
        val subscribersMap = sortedMapOf<String, Map<String, List<Member>>>()
        val cancelledExtractsMap = mutableListOf<Member>()
        val deDupDocType = listOf("Member ID Card - Member Request", "Member ID Card - Portal Member Request")

        if (File(inputPath).isDirectory) {
            File(inputPath)
                .walk()
                .filter { file -> file.isFile && file.extension == "xml" }
                .forEach { file ->
                    when {
                        file.name.startsWith("member") -> {

                            val rootElement = SAXReader().read(file).document.rootElement
                            rootElement.normalize()

                            val memberElement = rootElement
                                .element("correspondenceItem")
                                .element("recipient")
                                .element("member")

                            val member = Member()
                            member.correspondenceId = rootElement
                                .element("correspondenceItem")
                                .element("correspondenceId")
                                .text
                            getMemberInfo(memberElement, member)
                            member.address = getAddress(memberElement)
                            member.filename = file.absolutePath
                            member.plan = getPlan(memberElement)
                            //ToDo: Build plan.isValid()
                            //ToDo: Build Error Handler
                            //if (member.plan.isValid() != true) callErrorHandler -> continue
                            member.providerChoice = getProviderChoice(memberElement)
                            member.trackingValues = getTrackingValues(rootElement)
                            getIdTemplate(member)
                            getRider(member.plan, member.trackingValues)
                            member.tiers = getTiers(member.plan)
                            //ToDo: Build Recard List
                            if (config.recard) {
                                // Check if member.id is in re-card file
                                // Need a list to store response 'initialCardRunSet'
                            }

                            log(member.toString())
                            log(" ")

                            when {
                                subscribersMap.containsKey(member.subscriptionId) -> {
                                    val addressMap =
                                        subscribersMap[member.subscriptionId] as MutableMap<String, List<Member>>
                                    when {
                                        addressMap.containsKey(member.address.toString()) -> {
                                            val memberList =
                                                addressMap[member.address.toString()] as MutableList<Member>
                                            var foundDeDup = false
                                            memberList.forEach {
                                                if (deDupDocType.contains(member.trackingValues.documentType)
                                                    && it.trackingValues.documentType == member.trackingValues.documentType
                                                    && it.subscriberId == member.subscriberId
                                                ) {
                                                    foundDeDup = true
                                                }
                                            }
                                            if (foundDeDup) cancelledExtractsMap.add(member)
                                            else memberList.add(member)
                                            addressMap[member.address.toString()] = memberList
                                            subscribersMap[member.subscriptionId] = addressMap
                                        }
                                        else -> {
                                            val newAddressList = ArrayList<Member>()
                                            newAddressList.add(member)
                                            addressMap[member.address.toString()] = newAddressList
                                            subscribersMap[member.subscriptionId] = addressMap
                                        }
                                    }
                                }
                                else -> {
                                    val addressMap = mutableMapOf<String, List<Member>>()
                                    val memberList = mutableListOf<Member>()
                                    memberList.add(member)
                                    addressMap[member.address.toString()] = memberList
                                    subscribersMap[member.subscriptionId] = addressMap
                                }
                            }
                        }
                    }
                }
        } else {
            log("Invalid Path: $inputPath")
            // ToDo: Add error handling
        }

        outputIdCards(subscribersMap, cancelledExtractsMap)

        // End Process
    }

    fun outputIdCards(subscribersMap: SortedMap<String, Map<String, List<Member>>>, cancelledExtractsMap: MutableList<Member>) {

        val time = Instant.now()

        // Create Member Kit List
        val memberKitList = mutableListOf<MemberKit>()

        // Create Updater Config XML Document
        val ns = Namespace("n", "http://www.healthedge.com/connector/schema/correspondence/updater")
        val updaterConfigDocument = DocumentHelper.createDocument()
        val updaterConfig = updaterConfigDocument.addElement(QName("correspondences", ns))
        updaterConfig.addElement("jobType")
            .addText("membership")
        updaterConfig.addElement("configName")
            .addText("All")

        // Create O'Niel ID Card XML Document
        val document = DocumentHelper.createDocument()
        val root = document.addElement("memberIdCards")
        val healthRulesFileNameTimeStamp = DateTimeFormatter
            .ofPattern("yyyyMMddhhmmssS")
            .withZone(ZoneId.systemDefault())
            .format(time)
        root.addElement("healthRulesFileName")
            .addText("idcards_$healthRulesFileNameTimeStamp.xml")
        val fileCreateDateTimeTimeStamp = DateTimeFormatter
            .ofPattern("yyyy-MM-dd hh:mm:ssS")
            .withZone(ZoneId.systemDefault())
            .format(time)
        root.addElement("fileCreateDateTime")
            .addText(fileCreateDateTimeTimeStamp)

        val totalCarriers = root.addElement("totalCarriers")

        // Loop through Members and create Envelopes
        subscribersMap.forEach { it: Map.Entry<String, Map<String, List<Member>>> ->
            val subscriptionId = it.key
            val addressMap = it.value
            var carrierCount = 0

            addressMap.forEach {
                val address = it.key
                val memberList = it.value
                var envelope: Element?
                var letterId: Element?
                var carrier: Element? = null
                var cardCount = 0
                memberList
                    .sortedWith(CompareMembers)
                    .forEach { member ->

                        // Create ID Card XML for O'Niel
                        if (cardCount == 0) {
                            envelope = root.addElement("envelope")
                            letterId = envelope!!.addElement("letterID")
                            carrier = envelope!!.addElement("carrier")
                            carrierCount++
                            cardCount = 0
                            val mailingAddress = carrier!!.addElement("mailingAddress")
                            val parentOfMessage = mailingAddress.addElement("parentOfMessage")
                            if (member.age() < 18)
                                parentOfMessage.addText("To the Parent/Guardian of:")
                            mailingAddress.addElement("Name").addText(member.fullName())
                            mailingAddress.addElement("address").addText(member.address.address())
                            mailingAddress.addElement("city").addText(member.address.city)
                            mailingAddress.addElement("state").addText(member.address.stateCode)
                            mailingAddress.addElement("zipCode").addText(member.address.zip())
                        }
                        val idCard = carrier!!.addElement("idCard")
                        idCard.addElement("cardNumber").addText("0${cardCount + 1}")
                        idCard.addElement("cardTemplate").addText(member.templateId)
                        idCard.addElement("memberFullName").addText(member.fullName())
                        idCard.addElement("memberId").addText(member.memberId)
                        idCard.addElement("DOB").addText(member.birthDate)
                        idCard.addElement("sex").addText(member.genderCode)
                        idCard.addElement("effectiveDate").addText(member.plan.startDate)
                        idCard.addElement("benefitPlanDesc").addText(member.plan.benefitPlanDesc)
                        idCard.addElement("pcpPhone").addText(member.providerChoice.pcpPhone)
                        idCard.addElement("pcpFullName").addText(member.providerChoice.pcpFullName)
                        idCard.addElement("planMedicalGroup").addText(member.providerChoice.planMedicalGroup)
                        idCard.addElement("network").addText(member.plan.network)

                        val tiers = idCard.addElement("tiers")

                        val tier1 = tiers.addElement("tier")
                        tier1.addElement("tierNumber")
                            .addText(member.tiers.tier1.tierNumber)
                        tier1.addElement("deductible")
                            .addText(member.tiers.tier1.deductible)
                        tier1.addElement("pcpCostShare")
                            .addText(member.tiers.tier1.pcpCostShare)
                        tier1.addElement("specialistCostShare")
                            .addText(member.tiers.tier1.specialistCostShare)
                        tier1.addElement("hospitalCostShare")
                            .addText(member.tiers.tier1.hospitalCostShare)
                        tier1.addElement("urgentCareCostShare")
                            .addText(member.tiers.tier1.urgentCareCostShare)
                        tier1.addElement("erCostShare")
                            .addText(member.tiers.tier1.erCostShare)

                        if (member.tiers.tier2.tierNumber == "2") {
                            val tier2 = tiers.addElement("tier")
                            tier2.addElement("tierNumber").addText(member.tiers.tier2.tierNumber)
                            tier2.addElement("deductible").addText(member.tiers.tier2.deductible)
                            tier2.addElement("pcpCostShare").addText(member.tiers.tier2.pcpCostShare)
                            tier2.addElement("specialistCostShare").addText(member.tiers.tier2.specialistCostShare)
                            tier2.addElement("hospitalCostShare").addText(member.tiers.tier2.hospitalCostShare)
                            tier2.addElement("urgentCareCostShare").addText(member.tiers.tier2.urgentCareCostShare)
                            tier2.addElement("erCostShare").addText(member.tiers.tier2.erCostShare)
                        }

                        idCard.addElement("returnImageFileName")

                        val trackingValues = idCard.addElement("trackingValues")
                        trackingValues.addElement("documentType").addText(member.trackingValues.documentType)
                        trackingValues.addElement("id").addText(member.trackingValues.id)
                        trackingValues.addElement("accountName").addText(member.trackingValues.accountName)
                        trackingValues.addElement("accountId").addText(member.trackingValues.accountId)

                        when (member.trackingValues.visionRider == "") {
                            true -> trackingValues.addElement("visionRider")
                            false -> trackingValues.addElement("visionRider").addText(member.trackingValues.visionRider)
                        }
                        when (member.trackingValues.mentalHealthRider == "") {
                            true -> trackingValues.addElement("mentalHealthRider")
                            false -> trackingValues.addElement("mentalHealthRider").addText(member.trackingValues.mentalHealthRider)
                        }
                        when (member.trackingValues.pharmacyRider == "") {
                            true -> trackingValues.addElement("pharmacyRider")
                            false -> trackingValues.addElement("pharmacyRider").addText(member.trackingValues.pharmacyRider)
                        }
                        when (member.trackingValues.chiroAcupunctureRider == "") {
                            true -> trackingValues.addElement("chiroAcupunctureRider")
                            false -> trackingValues.addElement("chiroAcupunctureRider").addText(member.trackingValues.chiroAcupunctureRider)
                        }

                        cardCount++

                        if (cardCount == 2) cardCount = 0

                        // Create Member Kit File
                        val memberKit = setMemberKit(member)
                        memberKitList.add(memberKit)

                        // Create Updater Config XML
                        addMemberToUpdaterConfig(updaterConfig, member, "p")

                        // ToDo: Get Letter ID
                        //    ##member.trackingValues.documentType,Initial Card Run,Account = "CalPERS"|LetterID
                        //    idcardletterid1=Member ID Card - New Member,Existing member,No|1
                        //    idcardletterid2=Member ID Card - New Member,Existing member,Yes|2
                        //    idcardletterid3=Member ID Card - New Member,Brand New Member,No|3
                        //    idcardletterid4=Member ID Card - New Member,Brand New Member,Yes|4
                        //    idcardletterid5=Member ID Card - Demographic Change,NA,No|5
                        //    idcardletterid6=Member ID Card - Demographic Change,NA,Yes|6
                        //    idcardletterid7=Member ID Card - Member Request,NA,No|7
                        //    idcardletterid8=Member ID Card - Member Request,NA,Yes|8
                        //    idcardletterid9=Member ID Card - Portal Member Request,NA,No|7
                        //    idcardletterid10=Member ID Card - Portal Member Request,NA,Yes|8
                        //    idcardletterid11=Member ID Card - Benefit Change,NA,No|9
                        //    idcardletterid12=Member ID Card - Benefit Change,NA,Yes|10
                        //    idcardletterid13=Member ID Card - PCP or PMG Change,NA,No|11
                        //    idcardletterid14=Member ID Card - PCP or PMG Change,NA,Yes|12
                        //    idcardletterid15=Member ID Card - Provider No Longer Available,NA,No|11
                        //    idcardletterid16=Member ID Card - Provider No Longer Available,NA,Yes|12
                        if (config.recard) {
//                        letterId.addText(getLetterId(member.trackingValues.documentType, "Brand New Member", calPers?))
                        } else {
//                        letterId.addText(getLetterId(member.trackingValues.documentType, "Brand New Member", calPers?))
                        }


                    }
            }
            totalCarriers.addText("$carrierCount")
        }

        // Output Document
        val onielTimeStamp = DateTimeFormatter
            .ofPattern("yyyyMMddHHmmssSSS")
            .withZone(ZoneId.systemDefault())
            .format(time)
        write("Files/output/idCard_$onielTimeStamp.xml", document)

        // Output Member Kit file
        val memberKitTimeStamp = DateTimeFormatter
            .ofPattern("yyyyMMddHHmmssSSS")
            .withZone(ZoneId.systemDefault())
            .format(time)
        val memberKitFileName = "Files/output/mkr_$memberKitTimeStamp.txt"
        File(memberKitFileName).bufferedWriter().use { out ->
            out.write("ID Card Template|Product|Employer Group Name|Member ID|Member Name|Member Birth Date|Effective Date|Correspondence Address|Correspondence City|Correspondence State|Correspondence Zip|Benefit Plan|Vision Plan Rider|Mental Health Rider|Pharmacy Rider|Chiro/Acupuncture Rider|Letter #|Benefit Plan ID\n")
            memberKitList.forEach { memberKit ->
                out.write("$memberKit\n")

            }
        }

        // Append Cancelled Member FIles to Updater Config
        cancelledExtractsMap.forEach { member ->
            addMemberToUpdaterConfig(updaterConfig, member, "c")
        }

        // Output Updater Config
        val updaterTimeStamp = DateTimeFormatter
            .ofPattern("yyyyMMddHHmmssSSS")
            .withZone(ZoneId.systemDefault())
            .format(time)
        write("Files/output/updaterConfig_$updaterTimeStamp.xml", updaterConfigDocument)
    }

    private fun addMemberToUpdaterConfig(updaterConfig: Element, member: Member, status: String) {
        val correspondence = updaterConfig.addElement("correspondence")
        val recipient = correspondence.addElement("recipient")
        recipient.addElement("type")
            .addText("Membership")
        recipient.addElement("hccId")
            .addText(member.subscriberId)

        correspondence.addElement("id")
            .addText(member.correspondenceId)

        correspondence.addElement("status")
            .addText(status)

        correspondence.addElement("description")
            .addText(member.trackingValues.documentType)

        val definition = correspondence.addElement("definition")
        definition.addElement("name")
            .addText(member.trackingValues.documentType)

        val subject = correspondence.addElement("subject")
        subject.addElement("type")
            .addText("Membership")
        subject.addElement("hccId")
            .addText(member.subscriberId)
    }

    private fun getAddress(memberElement: Element): Address {
        val address = Address()
        memberElement
            .element("correspondenceAddress")
            .elementIterator()
            .forEach { element ->
                when (element.name) {
                    "address" -> address.address = element.text
                    "address2" -> address.address2 = element.text
                    "address3" -> address.address3 = element.text
                    "city" -> address.city = element.text
                    "stateCode" -> address.stateCode = element.text
                    "zipCode" -> address.zipCode = element.text
                    "zipExtension" -> address.zipExtension = element.text
                    "countryCode" -> address.countryCode = element.text
                    "countyCode" -> address.countyCode = element.text
                }
            }
        return address
    }

    private fun getMemberInfo(memberElement: Element, member: Member) {
        member.topAccountName = memberElement
            .element("hierarchialAccountInformation")
            .element("topAccountName")
            .text

        member.topAccountId = memberElement
            .element("hierarchialAccountInformation")
            .element("topAccountId")
            .text

        memberElement
            .elementIterator()
            .forEach { element ->
                when (element.name) {
                    "subscriptionId" -> member.subscriptionId = element.text
                    "id" -> member.memberId = element.text
                    "subscriberId" -> member.subscriberId = element.text
                    "firstName" -> member.firstName = element.text
                    "middleName" -> member.middleName = element.text
                    "lastName" -> member.lastName = element.text
                    "nameSuffix" -> member.nameSuffix = element.text
                    "birthDate" -> member.birthDate = element.text
                    "genderCode" -> member.genderCode = element.text
                    "isSubscriber" -> member.isSubscriber = element.text
                }
            }
    }

    private fun getIdTemplate(member: Member) {
        if (member.plan.product.startsWith("Covered California")) {
            member.templateId = "On-Exchange";
        } else if (member.plan.product.startsWith("POS") && member.topAccountId == "1002141") {
            member.templateId = "Custom Client POS";
        } else if (member.topAccountId == "1002141") {
            member.templateId = "Custom Client";
        } else if (member.plan.product.startsWith("POS")) {
            member.templateId = "POS";
        } else if (member.topAccountId == "1006268") {
            member.templateId = "MEA";
        } else if (member.topAccountId == "1002026") {
            member.templateId = "CalPERS";
        } else if (member.trackingValues.isDental == "Yes"
            || member.topAccountId == "1008002"
            || member.topAccountId == "1009001"
        ) {
            member.templateId = "Off-Exchange (Access Dental)";
        } else {
            member.templateId = "Off-Exchange";
        }
    }

    private fun getLetterId() {
//    ##member.trackingValues.documentType,Initial Card Run,Account = "CalPERS"|LetterID
//    idcardletterid1=Member ID Card - New Member,Existing member,No|1
//    idcardletterid2=Member ID Card - New Member,Existing member,Yes|2
//    idcardletterid3=Member ID Card - New Member,Brand New Member,No|3
//    idcardletterid4=Member ID Card - New Member,Brand New Member,Yes|4
//    idcardletterid5=Member ID Card - Demographic Change,NA,No|5
//    idcardletterid6=Member ID Card - Demographic Change,NA,Yes|6
//    idcardletterid7=Member ID Card - Member Request,NA,No|7
//    idcardletterid8=Member ID Card - Member Request,NA,Yes|8
//    idcardletterid9=Member ID Card - Portal Member Request,NA,No|7
//    idcardletterid10=Member ID Card - Portal Member Request,NA,Yes|8
//    idcardletterid11=Member ID Card - Benefit Change,NA,No|9
//    idcardletterid12=Member ID Card - Benefit Change,NA,Yes|10
//    idcardletterid13=Member ID Card - PCP or PMG Change,NA,No|11
//    idcardletterid14=Member ID Card - PCP or PMG Change,NA,Yes|12
//    idcardletterid15=Member ID Card - Provider No Longer Available,NA,No|11
//    idcardletterid16=Member ID Card - Provider No Longer Available,NA,Yes|12
    }

    private fun getLatestPlan(memberElement: Element, latestPlan: Plan, plan: String) {
        // Select Latest by End Date
        val tempPlan = Plan()
        tempPlan.planType = plan

        memberElement
            .element(plan)
            .elementIterator()
            .forEach { planInformation ->
                when (planInformation.name) {
                    "planInformation" -> {
                        tempPlan.element = planInformation
                        planInformation
                            .elementIterator()
                            .forEach { element ->
                                when (element.name) {
                                    "endDate" -> tempPlan.endDate = element.text
                                    "startDate" -> tempPlan.startDate = element.text
                                    "planName" -> tempPlan.benefitPlanDesc = element.text
                                    "planId" -> tempPlan.benefitPlanId = element.text
                                    "benefitNetworkList" -> tempPlan.network = element
                                        .element("benefitNetworkInformation")
                                        .element("benefitNetworkName").text
                                    "product" -> tempPlan.product = element
                                        .element("productName").text
                                }
                            }
                    }
                }
                if ((tempPlan.endDate != tempPlan.startDate) &&
                    (tempPlan.startDateValid()) &&
                    (tempPlan.product != "Medicare Advantage")
                ) {
                    if (latestPlan.endDate == "" || latestPlan.isLatest(tempPlan.endDate)) {
                        latestPlan.element = tempPlan.element
                        latestPlan.planType = tempPlan.planType
                        latestPlan.endDate = tempPlan.endDate
                        latestPlan.startDate = tempPlan.startDate
                        latestPlan.benefitPlanDesc = tempPlan.benefitPlanDesc
                        latestPlan.benefitPlanId = tempPlan.benefitPlanId
                        latestPlan.network = tempPlan.network
                        latestPlan.product = tempPlan.product
                    }
                }
            }
    }

    private fun getLatestProviderChoice(memberElement: Element, latestProviderChoice: ProviderChoice, plan: String) {
        val tempProviderChoice = ProviderChoice()
        tempProviderChoice.planType = plan
        memberElement
            .element(plan)
            .elementIterator()
            .forEach { providerChoice ->
                when (providerChoice.name) {
                    "providerChoice" ->
                        providerChoice.elementIterator()
                            .forEach { element ->
                                when (element.name) {
                                    "endDate" -> tempProviderChoice.endDate = element.text
                                    "choiceType" -> tempProviderChoice.choiceType = element.text
                                    "supplierName" -> tempProviderChoice.supplierName = element.text
                                    "supplierPhone" -> tempProviderChoice.supplierPhone = element.text
                                    "providerPhone" -> tempProviderChoice.providerPhone = element.text
                                    "firstName" -> tempProviderChoice.firstName = element.text
                                    "lastName" -> tempProviderChoice.lastName = element.text
                                    "affiliatedNetwork" -> tempProviderChoice.affiliatedNetwork = element.text
                                }
                                if (tempProviderChoice.choiceType == "PCP") {
                                    if (tempProviderChoice.supplierName == "Sharp Rees Stealy Medical Group"
                                        || tempProviderChoice.supplierName == "Sharp Rees-Stealy Medical Group"
                                    ) {
                                        tempProviderChoice.pcpPhone = tempProviderChoice.supplierPhone
                                        tempProviderChoice.pcpFullName = "Sharp Rees-Stealy Medical Group"
                                    } else {
                                        tempProviderChoice.pcpPhone = tempProviderChoice.providerPhone
                                        tempProviderChoice.pcpFullName =
                                            tempProviderChoice.firstName + " " + tempProviderChoice.lastName
                                    }
                                }
                                if (pmgIdNameMap.containsKey(tempProviderChoice.affiliatedNetwork)) {
                                    tempProviderChoice.planMedicalGroup =
                                        pmgIdNameMap.getValue(tempProviderChoice.affiliatedNetwork)
                                } else {
                                    tempProviderChoice.planMedicalGroup = tempProviderChoice.affiliatedNetwork
                                }

                            }
                }
                if (latestProviderChoice.endDate == "" || tempProviderChoice.isLatest(tempProviderChoice.endDate)) {
                    latestProviderChoice.planType = tempProviderChoice.planType
                    latestProviderChoice.endDate = tempProviderChoice.endDate
                    latestProviderChoice.choiceType = tempProviderChoice.choiceType
                    latestProviderChoice.supplierName = tempProviderChoice.supplierName
                    latestProviderChoice.supplierPhone = tempProviderChoice.supplierPhone
                    latestProviderChoice.providerPhone = tempProviderChoice.providerPhone
                    latestProviderChoice.pcpPhone = tempProviderChoice.pcpPhone
                    latestProviderChoice.pcpFullName = tempProviderChoice.pcpFullName
                    latestProviderChoice.firstName = tempProviderChoice.firstName
                    latestProviderChoice.lastName = tempProviderChoice.lastName
                    latestProviderChoice.affiliatedNetwork = tempProviderChoice.affiliatedNetwork
                    latestProviderChoice.planMedicalGroup = tempProviderChoice.planMedicalGroup
                }
            }
    }

    private fun getPlan(memberElement: Element): Plan {
        val latestPlan = Plan()
        memberElement
            .elementIterator()
            .forEach { element ->
                when (element.name) {
                    "currentPlans" ->
                        getLatestPlan(memberElement, latestPlan, "currentPlans")
                    "historicalPlans" ->
                        getLatestPlan(memberElement, latestPlan, "historicalPlans")
                    "pendingPlans" ->
                        getLatestPlan(memberElement, latestPlan, "pendingPlans")
                }
            }
        return latestPlan
    }

    private fun getProviderChoice(memberElement: Element): ProviderChoice {
        val latestProviderChoice = ProviderChoice()
        memberElement
            .elementIterator()
            .forEach { element ->
                when (element.name) {
                    "currentProviderChoices" ->
                        getLatestProviderChoice(memberElement, latestProviderChoice, "currentProviderChoices")
                    "historicalProviderChoices" ->
                        getLatestProviderChoice(memberElement, latestProviderChoice, "historicalProviderChoices")
                    "pendingProviderChoices" ->
                        getLatestProviderChoice(memberElement, latestProviderChoice, "pendingProviderChoices")
                }
            }
        return latestProviderChoice
    }

    private fun getRider(latestPlan: Plan, trackingValues: TrackingValues) {
        latestPlan
            .element
            .selectNodes("rider/rider/riderName")
            .forEach { riderName ->
                if (visionRider.contains(riderName.text) &&
                    trackingValues.visionRider == ""
                )
                    trackingValues.visionRider = riderName.text

                if (mentalHealthRider.contains(riderName.text) &&
                    trackingValues.mentalHealthRider == ""
                )
                    trackingValues.mentalHealthRider = riderName.text

                if (pharmacyRider.contains(riderName.text) &&
                    trackingValues.pharmacyRider == ""
                )
                    trackingValues.pharmacyRider = riderName.text

                if (chiropractorAcupunctureRider.contains(riderName.text) &&
                    trackingValues.chiroAcupunctureRider == ""
                )
                    trackingValues.chiroAcupunctureRider = riderName.text

                if ("Dental" == riderName.text)
                    trackingValues.isDental = "Yes"
            }
    }

    private fun getTiers(latestPlan: Plan): Tiers {
        val tiers = Tiers()
        val tier1 = Tier()
        tier1.tierNumber = "1"
        latestPlan
            .element
            .selectNodes("valueList/valueDefinition")
            .forEach { valueDefinition ->
                when (valueDefinition.selectSingleNode("variableName").text) {
                    "Individual Deductible" -> {
                        valueDefinition.selectSingleNode("currencyValue")?.let { tier1.deductible = it.text }
                        valueDefinition.selectSingleNode("percentValue")?.let { tier1.deductible = "${it.text}%" }
                    }
                    "PCP Copay", "PCP Coinsurance" -> {
                        valueDefinition.selectSingleNode("currencyValue")?.let { tier1.pcpCostShare = it.text }
                        valueDefinition.selectSingleNode("percentValue")?.let { tier1.pcpCostShare = "${it.text}%" }
                    }
                    "Specialist Copay", "Specialist Coinsurance" -> {
                        valueDefinition.selectSingleNode("currencyValue")?.let { tier1.specialistCostShare = it.text }
                        valueDefinition.selectSingleNode("percentValue")
                            ?.let { tier1.specialistCostShare = "${it.text}%" }
                    }
                    "Hospital Copay", "Hospital Coinsurance" -> {
                        valueDefinition.selectSingleNode("currencyValue")?.let { tier1.hospitalCostShare = it.text }
                        valueDefinition.selectSingleNode("percentValue")
                            ?.let { tier1.hospitalCostShare = "${it.text}%" }
                    }
                    "Urgent Care Copay", "Urgent Care Coinsurance" -> {
                        valueDefinition.selectSingleNode("currencyValue")?.let { tier1.urgentCareCostShare = it.text }
                        valueDefinition.selectSingleNode("percentValue")
                            ?.let { tier1.urgentCareCostShare = "${it.text}%" }
                    }
                    "ER Copay", "ER Coinsurance" -> {
                        valueDefinition.selectSingleNode("currencyValue")?.let { tier1.erCostShare = it.text }
                        valueDefinition.selectSingleNode("percentValue")?.let { tier1.erCostShare = it.text }
                    }
                }
            }

        val tier2 = Tier()
        if (latestPlan.product == "POS Small" ||
            latestPlan.product == "POS Large"
        ) {
            tier2.tierNumber = "2"
            var deductable = ""
            var coinsurance = ""
            latestPlan
                .element
                .selectNodes("valueList/valueDefinition")
                .forEach { valueDefinition ->
                    when (valueDefinition.selectSingleNode("variableName").text) {
                        "Individual OON Deductible" -> {
                            valueDefinition.selectSingleNode("currencyValue")?.let { deductable = it.text }
                            valueDefinition.selectSingleNode("percentValue")?.let { deductable = "${it.text}%" }
                        }
                        "Tier 2 Coinsurance" -> {
                            valueDefinition.selectSingleNode("currencyValue")?.let { coinsurance = it.text }
                            valueDefinition.selectSingleNode("percentValue")?.let { coinsurance = "${it.text}%" }
                        }
                    }
                }
            tier2.deductible = when {
                deductable != "" -> deductable
                else -> coinsurance
            }
            tier2.pcpCostShare = coinsurance
            tier2.specialistCostShare = coinsurance
            tier2.hospitalCostShare = coinsurance
            when (latestPlan.benefitPlanDesc.toUpperCase()) {
                "POS NG 1 L", "PALOMAR HEALTH POS NG 1 L"
                -> tier2.urgentCareCostShare = tier1.urgentCareCostShare
                else -> tier2.urgentCareCostShare = coinsurance
            }
            tier2.erCostShare = tier1.erCostShare
        }
        tiers.tier1 = tier1
        tiers.tier2 = tier2

        return tiers
    }

    private fun getTrackingValues(rootElement: Element): TrackingValues {
        val trackingValues = TrackingValues()
        rootElement
            .element("correspondenceItem")
            .elementIterator()
            .forEach { element ->
                when (element.name) {
                    "documentType" ->
                        trackingValues.documentType = element.text
                    "correspondenceId" ->
                        trackingValues.id = element.text
                }
            }

        rootElement
            .element("correspondenceItem")
            .element("recipient")
            .element("member")
            .element("account")
            .elementIterator()
            .forEach { element ->
                when (element.name) {
                    "name" -> trackingValues.accountName = element.text
                    "id" -> trackingValues.accountId = element.text
                }
            }


        return trackingValues
    }

    private fun setMemberKit(member: Member): MemberKit {
        val memberKit = MemberKit()
        memberKit.IdCardTemplate = member.templateId
        memberKit.Product = member.plan.product
        memberKit.EmployerGroupName = member.topAccountName
        memberKit.MemberId = member.memberId
        memberKit.MemberName = member.fullName()
        memberKit.MemberBirthDate = member.birthDate
        memberKit.EffectiveDate = member.plan.startDate
        memberKit.CorrespondenceAddress = member.address.address()
        memberKit.CorrespondenceCity = member.address.city
        memberKit.CorrespondenceState = member.address.stateCode
        memberKit.CorrespondenceZip = member.address.zip()
        memberKit.BenefitPlan = member.plan.benefitPlanDesc
        memberKit.VisionPlanRider = member.trackingValues.visionRider
        memberKit.MentalHealthRider = member.trackingValues.mentalHealthRider
        memberKit.PharmacyRider = member.trackingValues.pharmacyRider
        memberKit.ChiroAcupunctureRider = member.trackingValues.chiroAcupunctureRider
        memberKit.LetterNumber = member.letterId
        memberKit.BenefitPlanId = member.plan.benefitPlanId
        return memberKit
    }

    @Throws(IOException::class)
    fun write(filename: String, document: Document) {

        val format = OutputFormat.createPrettyPrint()
        format.isNewLineAfterDeclaration = false

        // Pretty print the document to a file
        FileWriter(filename).use { fileWriter ->
            val writer = XMLWriter(fileWriter, format)

            writer.write(document)
            writer.close()
        }

        // Pretty print the document to System.out
        val writer = XMLWriter(System.out, format)
        writer.write(document)

    }

    class Address {
        var address = ""
        var address2 = ""
        var address3 = ""
        var city = ""
        var stateCode = ""
        var zipCode = ""
        var zipExtension = ""
        var countyCode = ""
        var countryCode = ""
        fun address(): String {
            return "$address $address2 $address3".trim()
        }

        fun zip(): String {
            return when (zipExtension) {
                "" -> zipCode
                else -> "$zipCode-$zipExtension"
            }
        }

        override fun toString(): String =
            "Address: address|$address, address2|$address2, address3|$address3, city|$city, stateCode|$stateCode, zipCode|$zipCode, zipExtension|$zipExtension, countyCode|$countyCode, countryCode|$countryCode"
    }

    class Plan {
        lateinit var element: Element
        var planType = ""
        var endDate = ""
        var startDate = "" // aka effectiveDate
        var benefitPlanDesc = "" // aka planName
        var benefitPlanId = "" // aka planId
        var network = "" // benefitNetworkList/benefitNetworkInformation/benefitNetworkName
        var product = "" // product/productName
        fun startDateValid(): Boolean {
            return LocalDateTime.parse(startDate + "T20:00:00.0000").isBefore(LocalDateTime.now())
        }

        fun isLatest(compareDate: String): Boolean {
            return LocalDateTime.parse(endDate + "T20:00:00.0000")
                .isBefore(LocalDateTime.parse(compareDate + "T20:00:00.0000"))
        }

        override fun toString(): String =
            "Plan: planType|$planType, endDate|$endDate, startDate|$startDate, benefitPlanDesc|$benefitPlanDesc, benefitPlanId|$benefitPlanId, network|$network, product|$product"

    }

    class Member {
        lateinit var address: Address
        lateinit var plan: Plan
        lateinit var providerChoice: ProviderChoice
        lateinit var trackingValues: TrackingValues
        lateinit var tiers: Tiers
        var filename = ""
        var correspondenceId = ""
        var topAccountName = ""
        var topAccountId = ""
        var subscriptionId = ""
        var memberId = ""
        var subscriberId = ""
        var firstName = ""
        var middleName = ""
        var lastName = ""
        var nameSuffix = ""
        var birthDate = ""
        var isSubscriber = ""
        var genderCode = ""
        var templateId = ""
        var letterId = ""
        fun age(): Long {
            return Duration.between(
                LocalDateTime.parse(birthDate + "T20:00:00.0000"),
                LocalDateTime.now()
            ).toDays() / 365
        }
        fun fullName(): String {
            return when {
                middleName == "" -> "$firstName $lastName"
                middleName != "" -> "$firstName $middleName $lastName"
                else -> ""
            }
        }
        override fun toString(): String {
            return "Member Info:\n" +
                    "Member: filename|$filename, subscriptionId|$subscriptionId, memberId|$memberId, subscriberId|, " +
                    "$subscriberId, topAccountName|$topAccountName, topAccountId|$topAccountId, firstName|$firstName, " +
                    "middleName|$middleName, lastName|$lastName, " +
                    "nameSuffix|$nameSuffix, birthDate|$birthDate, age|${age()}, " +
                    "genderCode|$genderCode, isSubscriber|$isSubscriber" +
                    "\nMember " + address +
                    "\nMember " + plan +
                    "\nMember " + providerChoice +
                    "\nMember " + trackingValues +
                    "\nMember " + tiers
        }
    }

    class MemberKit {
        var IdCardTemplate = ""
        var Product = ""
        var EmployerGroupName = ""
        var MemberId = ""
        var MemberName = ""
        var MemberBirthDate = ""
        var EffectiveDate = ""
        var CorrespondenceAddress = ""
        var CorrespondenceCity = ""
        var CorrespondenceState = ""
        var CorrespondenceZip = ""
        var BenefitPlan = ""
        var VisionPlanRider = ""
        var MentalHealthRider = ""
        var PharmacyRider = ""
        var ChiroAcupunctureRider = ""
        var LetterNumber = ""
        var BenefitPlanId = ""
        override fun toString(): String =
            "$IdCardTemplate|$Product|$EmployerGroupName|$MemberId|$MemberName|$MemberBirthDate|$EffectiveDate|$CorrespondenceAddress|$CorrespondenceCity|$CorrespondenceState|$CorrespondenceZip|$BenefitPlan|$VisionPlanRider|$MentalHealthRider|$PharmacyRider|$ChiroAcupunctureRider|$LetterNumber|$BenefitPlanId"
    }

    class ProviderChoice {
        var planType = ""
        var endDate = ""
        var choiceType = ""
        var supplierName = ""
        var supplierPhone = ""
        var providerPhone = ""
        var pcpPhone = ""
        var pcpFullName = ""
        var firstName = ""
        var lastName = ""
        var affiliatedNetwork = ""
        var planMedicalGroup = ""
        fun isLatest(compareDate: String): Boolean {
            return LocalDateTime.parse(endDate + "T20:00:00.0000")
                .isBefore(LocalDateTime.parse(compareDate + "T20:00:00.0000"))
        }

        override fun toString(): String {
            return "ProviderChoice: planType|$planType, endDate|$endDate, choiceType|$choiceType, supplierName|$supplierName, " +
                    "supplierPhone|$supplierPhone, providerPhone|$providerPhone, pcpPhone|$pcpPhone, pcpFullName|$pcpFullName, " +
                    "lastName|$lastName, firstName|$firstName, affiliatedNetwork|$affiliatedNetwork, planMedicalGroup|$planMedicalGroup"
        }

    }

    class Tier {
        var tierNumber = "0" // 1
        var deductible = "$0" // $0
        var pcpCostShare = "$0" // $10.00
        var specialistCostShare = "$0" // $10.00
        var hospitalCostShare = "$0" // $100.00
        var urgentCareCostShare = "$0" // $10.00
        var erCostShare = "$0" // $50.00
        override fun toString(): String =
            "Tier: tierNumber|$tierNumber, deductible|$deductible, pcpCostShare|$pcpCostShare, " +
                    "specialistCostShare|$specialistCostShare, hospitalCostShare|$hospitalCostShare, " +
                    "urgentCareCostShare|$urgentCareCostShare, erCostShare|$erCostShare"

    }

    class Tiers {
        lateinit var tier1: Tier
        lateinit var tier2: Tier
        override fun toString(): String =
            "Tiers: \n\ttier1|$tier1 \n\ttier2|$tier2"
    }

    class TrackingValues {
        var documentType = ""
        var id = ""
        var accountName = ""
        var accountId = ""
        var visionRider = ""
        var mentalHealthRider = ""
        var pharmacyRider = ""
        var chiroAcupunctureRider = ""
        var isDental = "No"
        override fun toString(): String =
            "TrackingValues: documentType|$documentType, id|$id, accountName|$accountName, accountId|$accountId, visionRider|$visionRider, mentalHealthRider|$mentalHealthRider, pharmacyRider|$pharmacyRider, chiroAcupunctureRider|$chiroAcupunctureRider, isDental|$isDental"
    }

    class CompareMembers {
        companion object : Comparator<Member> {
            override fun compare(a: Member, b: Member): Int = when {
                a.memberId.substringAfterLast("-").toInt() > b.memberId.substringAfterLast("-").toInt() -> 0
                else -> -1
            }
        }
    }
}