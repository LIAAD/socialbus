#! /usr/bin/python
#
'''
Version     :    0.1
Author      :    Arian Pasquali
Summary     :    This class defines MongoDB interface
'''

from DataSource import *

import json,time
import pymongo
from pymongo import Connection
import sys
import re
import datetime
from datetime import date
from datetime import datetime
from json import loads



class MongoDataSource(DataSource):        # define parent class

	connection = None;
	database = None;

	def open_connection(self,options):
		print 'Opening mongo connection'

		if not options["host"] or not options["port"] or not options["database"]:
			return False
		
		# Create a connection to mongodb
		self.connection = Connection(options["host"],options["port"])
		self.database = self.connection[options["database"]]

	def get_collection(self,collection):
		if collection in self.database.collection_names():
			return self.database[collection]
		else:
			return None	

	def database():
		return self.database

	def index(self,object,collection):
		print "Indexing into MongoDB"
		self.database[collection].save(object)

	def close_connection(self):
		self.connection.close()