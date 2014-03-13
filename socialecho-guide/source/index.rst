Welcome to SocialEcho
########################

.. image:: _static/panther1.jpg
   :alt: SocialEcho: web development, one drop at a time
   :class: floatingflask
   :height: 300px
   
Welcome to SocialEcho's documentation. 

The project is being developed at the `Faculty of Engineering of the University of Porto <http://www.fe.up.pt/>`_  , in the scope of the  `REACTION <http://dmir.inesc-id.pt/reaction/Reaction>`_ project, a computational journalism study in collaboration with  `Sapo Labs <http://labs.sapo.pt>`_ . 


SocialEcho aims to be a fault tolerant data collector for social networks. Currently it supports Twitter and in the near future it will support Facebook.

The :ref:`rationale` page explains what SocialEcho is and why it was built.

.. toctree::
   :maxdepth: 1

   rationale

How it works
------------
SocialEcho comprises somes packages. SocialEcho Server, SocialEcho Twitter Consumer, SocialEcho Facebook Consumer and SocialEcho Twitter OAuth.

SocialEcho Twitter Consumer is used to monitor and consume tweets from Twitter. Users can define which keywords or users they are interested in. Users can deploy as many consumers as wanted.

SocialEcho Facebook Consumer is capable of searching posts on Facebook based also on keywords. 

When a consumer receives a tweet it sends the message to SocialEcho Server which is responsable for processing, extracting metadata, indexing, tokenizing and other computations. 

SocialEcho Twitter OAuth is a helper tool to create a file with Twitter user authorization token.

This documentation has all the instructions to deploy you own SocialEcho instance.


Architecture
------------

.. image:: _static/twitterecho/socialecho_architecture.png
   :width: 550px

Download last release
---------------------

Release v0.6

- `SocialEcho 0.6 <http://goo.gl/HwX0f5>`_

.. include:: contents_modes.rst.inc
