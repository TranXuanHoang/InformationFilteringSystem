# Information Filtering System
This software is used to evaluate the interestingness and/or usefulness of contents of webpages or documents (MS Word, MS Powerpoint, HTML, PDF and normal text files) pre-saved on PCs. The core algorithms use two type of artificial neural networks and probabilistic reasoning to calculate the interestingness and usefulness. The software evaluates each input webpage, and/or local document and returns a floating point value in the interval [0, 1] showing how much the input material is worth reading. The 1 end to which the returned  value approaches near, the more interesting the input material would be.

An example of the app's GUI capture:
<p align="center">
<img src="doc/Example%20of%20App%20UI%20Capture.png" alt="Example of App GUI Capture" />
</p>

## Getting Started
This repository contains all source code implementing theories that were used in my research work. The research focused on finding an effective method of checking whether an arbitrary piece of information is useful. Here is the slide giving an overview of my research 
[Information Filtering System with Reliability-ranked Agents](https://drive.google.com/file/d/0B42twD7zF0cwUk9KOTlldVZnb3M/view?usp=sharing).

And my full thesis that is a summary of the research can be downloaded from the JAIST Repository ([Click Here](http://hdl.handle.net/10119/13737)).
The thesis includes detailed explanation of all algorithms (type of neural networks used, how probablistic reasoning is applied, ...) that are implemented in the source code of this repo.

## Installation and Usage
### Installation
This application was written entirely in Java. Source code and runnable **.jar** program can be downloaded and refered from the following directories:
- Source code:  in [**src**](https://github.com/TranXuanHoang/InformationFilteringSystem/tree/Normal/src) directory
- Runnable file: in [**app**](https://github.com/TranXuanHoang/InformationFilteringSystem/tree/Normal/app) directory

Note that when clicking to download the **.jar** file, web browser may notify you
> *This type of file can harm your computer. Do you want to keep "filename" anyway?*

warning message. Choose the option to save the file on your computer (there will be no damage to your computer due to the download of this file).

After downloading the runnable **.jar** file, you can start the app by just double-clicking on it. You can also download the source code and compile to get the one suitable for you - It is up to you.

> Note that - since this application was written in Java, [Java SE Runtime Environment (JRE)](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) needs to be installed to run the app; and [Java Development Kit (JDK)](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) is required to complie the source code. I highly recommend you install the lastest versions of JRE and JDK.

### How to Use the App
For the usage of the app, please check this document:
[Appendix B  The Information Filtering System Tool.pdf](https://drive.google.com/file/d/0B42twD7zF0cwZHdUMG1HVjdqYTg/view?usp=sharing).

### Training Datasets
The app uses a given dataset to train its internal neural networks. For example, if you want to use this app to check how much a webpage is relevant to your interest of reading about *traveling to the moon*, you should provide it a sample dataset of documents talking about the topic (the sample documents are the ones you read in the past and found them interesting - of course about your favorite topic, e.g. *traveling to the moon*). If you do not have a sample training dataset, don't worry because you can make one from now on. At the beginning, without the sample training dataset, the app may and perhaps will return the result with big mistake. However, like human being, it will learn to find out the most appropriate information for its users. The more correct training datasets it receives, the better result it will return.

For more information about the format and an example of the training data set, see this document:
[Appendix A Training Datasets.pdf](https://drive.google.com/file/d/0B42twD7zF0cwUE1RYVNuQ3hWZ1U/view?usp=sharing).

## Source Code Overview
The whole source code is divided into four packages playing four main roles respectively.

**Package** | **Description**
----------- | ---------------
[learn](src/learn) | Contains classes implementing the [backpropagation neural network](https://en.wikipedia.org/wiki/Backpropagation) and the [self-organizing map (also called the Kohonen map neural network)](https://en.wikipedia.org/wiki/Self-organizing_map) that will be used during the learning step. This package also contains some of other classes that implement other machine learning algorithms such as decision tree. Those classes were written for other tasks that are out of the main purpose of our software. Ignore them if you are not interested in decision trees at all.
[agent](src/agent) | Declares base classes and interfaces providing an unified way to create and control _agents_. Each _agent_ is considered as a separate thread possessing its own task. For example, an _agent_ will be run to create a backpropagation neural net, while another _agent_ with the main task of creating and training a self-organizing map will be launch in a separate thread. So this package improves the app's performance and concurrency while giving an overal way to create, run, and kill internal threads the app uses to learn. Note also that, since training neural nets with big sample datasets is resource expensive, JavaBeans is used so that when the app is closed, all the trained neural netwoks will not be lost and can be recovered the next time we run the app again.
[multinet](src/multinet) | Basically, the app runs on a single PC helping one user to quickly check whether he/she should take time to read a webpage or a document. The app, however, can be installed on multiple PCs and connected together. So a number of users can share their favorite webpages' URLs with or simply just send messages to other users. This package was written for this task. It is used to make multiple instances of the app that are running on different PCs can connect and exchange information in a network. Each instance of the app running on a single PC is seen as a client that connects and sends data to the others, and as a server in the reverse direction. See [Appendix B  The Information Filtering System Tool.pdf](https://drive.google.com/file/d/0B42twD7zF0cwZHdUMG1HVjdqYTg/view?usp=sharing). for more information on how to configure the network to allow multiple instances of the app to connect and send text messages.
[filter](src/filter) | Defines classes initializing the GUI (graphical user interface) and controlling the main logic flow of  the app: `create and train neural networks`, `calculate the interesstingness of each webpage and/or document`, `sort and show results`, `update reliability of apps exchanging information in the network (i.e. if an app of other user sends many URLs as suggesting of webpages for you but most of them are not your favorites, the reliability of that app should be decreased so that we can manage which source information we can trust in.)`.


## Author
* **Tran Xuan Hoang**
* **Email:** hoangtx@jaist.ac.jp

## License
This project is licensed under the [MIT License](LICENSE).

## 3rd Party Libraries
Three open source libraries are refered and used in this project.
* **Graph Visualization** - [Jung (Ver. 2.2.0.1)](http://jung.sourceforge.net/)
* **Parsing Microsoft Documents** - [Apache POI - the Java API for Microsoft Documents (Ver. 3.14)](https://poi.apache.org/)
* **Parsing PDF Documents** - [Apache PDFBox (Ver. 2.0.1)](https://pdfbox.apache.org/)
