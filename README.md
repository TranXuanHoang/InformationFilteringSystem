# Information Filtering System
This software is used to rank the interestingness and/or usefulness of webpages or documents pre-saved on PCs using artificial neural networks and probabilistic reasoning.

## Getting Started
This source code repository contains all source of a program that implement the theory of my research. The research focused on find out information that is useful with respect to users. Here is the slide introducinng my research
[Information Filtering System with Reliability-ranked Agents](https://drive.google.com/file/d/0B42twD7zF0cwUk9KOTlldVZnb3M/view?usp=sharing)

And my full thesis can be downloaded from the JAIST Reposition: [Click Here](http://hdl.handle.net/10119/13737)
The thesis is a detailed explanation of all algorithms (type of neural networks used, how probablistic reasoning is used, ...) that are implemented in this source code repo.

## Installation and Usage of the Information Filtering System
The application was developed in Java. Source code and runnable **.jar** app can be downloaded and refered from the following directories:
- Source code:  in directory [**src**](https://github.com/TranXuanHoang/InformationFilteringSystem/tree/Normal/src)
- Runnable file: in directory [**app**](https://github.com/TranXuanHoang/InformationFilteringSystem/tree/Normal/app)
Note that when you click to download the **.jar** file, browser may notify you that
> *This type of file can harm your computer. Do you want to keep "filename" anyway?*

warning message. Choose the option to save the file on your computer.

After downloading the runnable **.jar** file, you can run the app by just double-clicking it. You can also download the source code and compile to get the app. It is up to you. 

For the usage of the app, check the following document:
[Appendix B  The Information Filtering System Tool.pdf](https://drive.google.com/file/d/0B42twD7zF0cwZHdUMG1HVjdqYTg/view?usp=sharing)

### Traning Datasets
The app uses a given dataset to train its internal neural networks. For example, if you want to use this app to check how much a webpage is relevant to your interest of finding information about *Traveling to the moon*, you should provide it a sample dataset of documents talking about the topic (e.g. the sample documents are the ones you read in the past and found them interesting). If you do not have a sample training dataset, don't worry because you can make one from now on.

For the details of the training step that the app runs inside, see the following document:
[Appendix A Training Datasets.pdf](https://drive.google.com/file/d/0B42twD7zF0cwUE1RYVNuQ3hWZ1U/view?usp=sharing)

## Author
* **Tran Xuan Hoang** - *Initial work* - [LazyCommitAndPushCode](https://github.com/TranXuanHoang)

## License
This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).

## 3rd Party Libraries
Three open source libraries are refered and used in this project.
* **Graph Visualization** - [Jung (Ver. 2-2_0_1)](http://jung.sourceforge.net/)
* **Parsing Microsoft Office Documents** - [Apache POI - the Java API for Microsoft Documents (Ver. 3.14)](https://poi.apache.org/)
* **Parsing PDF Documents** - [Apache PDFBox (Ver. 2.0.1)](https://pdfbox.apache.org/)
