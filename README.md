SocialBus
===========

SocialBus aims to be a fault tolerant data collector for social networks. Currently it supports Twitter and Facebook.
This project is a toolkit for collecting and processing messages from social networks for supporting research. It enables you to continuously collect data from particular user communities or topics, while respecting imposed limits and being fault tolerant. 
Additional modules can be easily implemented and extended.

Documentation
===========

In order to generate documentation:
1. Make sure you have Sphinx installed on your Python environment
```
pip install -U Sphinx
```

2. Go to socialbus-guide folder and run `make html` 
```
cd socialbus-guide
make html
```

3. If everything works you should have a documentation html website ready. Open file `build/html/index.html`.
```
open build/html/index.html
```
