# Welcome to the caAdapter Project!

caAdapter is an open source tool set that provides model mapping services, and facilitates data mapping and transformation among different kinds of data sources including HL7 v2 messages, HL7 v3 messages, and Regulatory Data Sets.caAdapter has a component-based architecture composed of loosely coupled service modules. Each caAdapter service module can be packed and deployed on the web for online use. caAdapter also provides programming APIs for application integration with other systems such as caBIG Integration Hub, and web service APIs for service integration over the network.

You will find more details about caAdapter in the following links:

 * [Community Wiki](https://wiki.nci.nih.gov/x/7Q5y)
 * [Code Repository](http://github.com/NCIP/caadapter)
 * [Issue Tracker](https://tracker.nci.nih.gov/browse/CAADAPTER)
 * [Documentation](https://wiki.nci.nih.gov/x/7Q5y)
 * [Release Notes](https://wiki.nci.nih.gov/x/npN4B)
 * [Installation Package](https://wiki.nci.nih.gov/display/caCORE/caAdapter+Module+Downloads)
 
An NCI hosted instance of caAdapter is publicly available at:

 * [caAdapter CMTS v1.0.1 Web GUI](http://caadapter.nci.nih.gov/caadapter-cmts)
 * [caAdapter CMTS v1.0.1 Web Service](http://caadapter.nci.nih.gov/caadapterWS-cmts)
 * [caAdapter MMS v4.4 Webstart](http://caadapter.nci.nih.gov/caadapter-mms)
 * [caAdapter GME v4.2 Webstart](http://caadapter.nci.nih.gov/caadapter-gme)
 
Please join us in further developing and improving caAdapter.

## caAdapter Core Components

* CSV to HL7 v3 Mapping and Transformation Service
* caAdapter Web Service
* Model Mapping Service
* SDTM Mapping and Transformation Service
* HL7 v2 to v3 Conversion Service

**Pros**

* Intuitive interface – easy to upload source files and the layout is easy to interpret
* Mapping source data to target elements is relatively easy
* Applying Mapping Functions is easy – ex. using a concatenate function to combine two source data fields into a single destination element
* Generating source into multiple formats is easy – converts to XML, CSV, or a relation data model

**Cons**

* Source mapping from HL7 v2 to v3 is manual – there were no intelligent defaults (perhaps this is the nature of the of the business domain?)
* Needs a domain expert to properly convert from v2 to v3
* Display of mapped elements does not fit on the screen (minor issue). It is difficult to navigate the links between source and destination elements
* Not web enabled – uses Java Swing

# Setup

## Requirements

* Docker (Engine and Compose)

Or...

* Java and Apache Ant

caAdapter is developed in Java and requires Apache Ant for building. 

## Building and Running (via Docker)

To build and run the project via Docker simply execute `./deployment/bin/compose.sh build` followed by `./deployment/bin/compose.sh run`.

# Licensing

caAdapter is distributed under the BSD 3-Clause License. Please see the NOTICE and LICENSE files for details.
