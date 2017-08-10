# Information Filtering System
This software is used to rank the interestingness and/or usefulness of webpages or documents pre-saved on PCs. The core algorithms use two type of artificial neural networks and probabilistic reasoning to calculate the interestingness and usefulness.

## Getting Started
This repository contains all source code of the software that implement the theory of my research. The research focused on finding out information that is useful with respect to users. Here is the slide I used to introduce my research to the others
[Information Filtering System with Reliability-ranked Agents](https://drive.google.com/file/d/0B42twD7zF0cwUk9KOTlldVZnb3M/view?usp=sharing).

And my full thesis can be downloaded from the JAIST Repository ([Click Here](http://hdl.handle.net/10119/13737)).
The thesis is a detailed explanation of all algorithms (type of neural networks used, how probablistic reasoning is applied, ...) that are implemented in this source code repo.

## Installation and Usage of the Information Filtering System
### Installation
The application was written entirely in Java. Source code and runnable **.jar** program can be downloaded and refered from the following directories:
- Source code:  in directory [**src**](https://github.com/TranXuanHoang/InformationFilteringSystem/tree/Normal/src)
- Runnable file: in directory [**app**](https://github.com/TranXuanHoang/InformationFilteringSystem/tree/Normal/app)
Note that when clicking to download the **.jar** file, web browser may notify you that
> *This type of file can harm your computer. Do you want to keep "filename" anyway?*

warning message. Choose the option to save the file on your computer (there will be no damage to your computer by download this file).

After downloading the runnable **.jar** file, you can run the app by just double-clicking on it. You can also download the source code and compile to get the one suitable for you - It is up to you.

> Note that - since this application was written in Java, [Java SE Runtime Environment (JRE)](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) needs to be installed to run the app; and [Java Development Kit (JDK)](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) is required to complie the source code. I highly recommend you to use the lastest version of JRE and JDK.

For the usage of the app, check the following document:
[Appendix B  The Information Filtering System Tool.pdf](https://drive.google.com/file/d/0B42twD7zF0cwZHdUMG1HVjdqYTg/view?usp=sharing)

### Training Datasets
The app uses a given dataset to train its internal neural networks. For example, if you want to use this app to check how much a webpage is relevant to your interest of finding information about *traveling to the moon*, you should provide it a sample dataset of documents talking about the topic (e.g. the sample documents are the ones you read in the past and found them interesting - of course about your favorite topic, e.g. *traveling to the moon*). If you do not have a sample training dataset, don't worry because you can make one from now on. At the beginning, without the sample training dataset, the app may and perhaps will return the result with big mistake. It however, likes human being, will learn to find out the most appropriate information for its users. The more correct training datasets it receives, the better result it will return.

For the details of the training data that the app runs inside, see the following document:
[Appendix A Training Datasets.pdf](https://drive.google.com/file/d/0B42twD7zF0cwUE1RYVNuQ3hWZ1U/view?usp=sharing)

## Author
* **Tran Xuan Hoang** - *Initial work* - [LazyCommitAndPush](https://github.com/TranXuanHoang)
* **Email:** - *Check regularly* - hoangtx@jaist.ac.jp

## License
This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).

## 3rd Party Libraries
Three open source libraries are refered and used in this project.
* **Graph Visualization** - [Jung (Ver. 2.2.0.1)](http://jung.sourceforge.net/)
* **Parsing Microsoft Documents** - [Apache POI - the Java API for Microsoft Documents (Ver. 3.14)](https://poi.apache.org/)
* **Parsing PDF Documents** - [Apache PDFBox (Ver. 2.0.1)](https://pdfbox.apache.org/)
