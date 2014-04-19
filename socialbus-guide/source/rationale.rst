Rationale
##########

.. image:: _static/logo_full.jpg
   :class: floatingflask
   :height: 170px

Modern social network analysis relies on vast quantities of data to infer new knowledge about human relations and communication. 
 
Social networks provide a huge and valuable amount of reasearch assets, real-time information network is the subject of research for information retrieval tasks such as real-time search. However, so far, reproducible experimentation on Twitter or Facebook data has been impeded by restrictions imposed by  terms of service. This project aims to solve some of this constraints helping researchers to build social networks datasets.

Project definition
--------------------

SocialBus aims to be a fault tolerant data collector for social networks. Currently it supports Twitter and in the near future it will support Facebook.

Building a data collector for social networks can be tricky, user have to manage API constraints, authorization tokens and sometimes networks failures. 

SocialBus is a toolkit for collecting and processing messages from social networks for supporting research. It enables you to continuously collect data from particular user communities or topics, while respecting imposed limits and being fault tolerant. Additional modules can be easily implemented and extended. 