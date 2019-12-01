package org.sharp.dig

import org.dom4j.Element
import org.dom4j.io.SAXReader
import java.io.File

class Letters {

    fun processLetters(inputPath: String) {

        var letters = mutableMapOf<String, MutableList<Letter>>()

        if (File(inputPath).isDirectory) {
            File(inputPath)
                .walk()
                .filter { file -> file.isFile && file.extension == "xml" }
                .forEach { file ->
                    when {
                        file.name.startsWith("letter") -> {

                            val rootElement = SAXReader().read(file).document.rootElement
                            rootElement.normalize()

                            val letter = Letter()
                            letter.documentType = rootElement
                                .element("correspondenceItem")
                                .element("documentType")
                                .text

                            val memberNode = rootElement
                                .element("correspondenceItem")
                                .element(when (letter.documentType) {
                                    "Member Termination for Group Non-Payment Letter"
                                    -> "subject"
                                    else -> "recipient"
                                })
                                .element("member")
                            letter.address = getAddressInfo(memberNode.element("correspondenceAddress"))

                            val member = Member()
                            member.address = letter.address
                            member.birthDate = memberNode.element("birthDate").text
                            member.id = memberNode.element("id").text
                            member.isSubscriber = memberNode.element("isSubscriber").text
                            member.firstName = memberNode.element("firstName").text
                            member.lastName = memberNode.element("lastName").text
                            member.subscriptionId = memberNode.element("subscriptionId").text
                            member.subscriptionId = memberNode.element("account").element("name").text

                        }
                    }
                }
        }

    }

    private fun getAddressInfo(addressNode: Element): Address {
        val address = Address()
        address.address  = addressNode.element("address").text
        address.address2  = addressNode.element("address2").text
        address.address3  = addressNode.element("address3").text
        address.city  = addressNode.element("city").text
        address.countryCode  = addressNode.element("countryCode").text
        address.countyCode  = addressNode.element("address").text
        address.stateCode  = addressNode.element("stateCode").text
        address.zipCode  = addressNode.element("zipCode").text
        address.zipExtension  = addressNode.element("zipExtension").text
        return address
    }

    private fun getLatestPlan(memberElement: Element, latestPlan: IdCards.Plan, plan: String) {
        // Select Latest by End Date
        val tempPlan = IdCards.Plan()
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

    fun getLetterId(documentType: String, accountName: String, productName: String): String {
        return when (documentType.toLowerCase()) {
            "creditable coverage termination letter" -> "TPL-H031"
            "member termination for group non-payment letter" -> "TPL-H004"
            "group termination for non-payment letter" -> "TPL-H003-A"
            "group termination for non-payment letter - broker" -> "TPL-H003-B"
            "calcobra letter - member" -> "TPL-H032-A"
            "calcobra letter - employer" -> "TPL-H032-B"
            "calcobra ab1401 letter - member" -> "TPL-H033-A"
            "calcobra ab1401 letter - employer" -> "TPL-H033-B"
            "claim denial letter" -> "TPL-H007"
            "initial payment notice letter" -> {
                when (productName.toLowerCase()) {
                    "covered california ifp" -> "TPL-H001-A"
                    else -> "TPL-H001-B"
                }
            }
            "individual termination for non-payment - subscriber" -> {
                when (accountName.toLowerCase()) {
                    "ifp on exchange" -> "TPL-H024-C"
                    else -> "TPL-H024-A"
                }
            }
            "individual cancellation - subscriber" -> {
                when (accountName.toLowerCase()) {
                    "ifp on exchange" -> "TPL-H015-A"
                    else -> "TPL-H015-B"
                }
            }
            "individual termination voluntary - subscriber" -> {
                when (accountName.toLowerCase()) {
                    "ifp on exchange" -> "TPL-H016-C"
                    else -> "TPL-H016-A"
                }
            }
            else -> "error"
        }
    }

    class Letter {
        lateinit var address: Address
        lateinit var members: List<Member>
        var correspondenceId = ""
        var documentType = ""
    }

    class Member {
        lateinit var address: Address
        var birthDate = ""
        var id = ""
        var subscriberId = ""
        var subscriptionId = ""
        var isSubscriber = ""
        var firstName = ""
        var lastName = ""
        var genderCode = ""
        var relationshipToSubscriber = ""
        var accountName = ""
        var productName = ""
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

        fun Address.toCSV(): String =
            "$address,$address2,$address3,$city,$stateCode,$zipCode,$zipExtension,$countyCode,$countryCode"

        override fun toString(): String =
            "Address: address|$address, address2|$address2, address3|$address3, city|$city, stateCode|$stateCode, zipCode|$zipCode, zipExtension|$zipExtension, countyCode|$countyCode, countryCode|$countryCode"
    }
}

