#! /usr/bin/python
#
'''
Version     :    0.1
Author      :    Arian Pasquali
Summary     :    This class defines generic datasource interface
'''

class DataSource:        # define parent class

	def open_connection(self,options):
		print 'Openning connection'

	def index(self,object):
		print 'Indexing'

	def close_connection(self):
		print 'Closing connection'