#!/usr/bin/env python
from __future__ import print_function
import pexpect, csv, string, math, sys

with open('dir.csv', 'rb') as readfile:
	reader = csv.reader(readfile)

with open('dir_w.csv', 'wb') as writefile:
	writer = csv.DictWriter(writefile, fieldnames=['Surname','GivenName','Year','OnCampus','Department','Alias'])
	writer.writeheader();
	with open('dir.csv', 'rb') as readfile:
		reader = csv.reader(readfile, delimiter=',')
		r = 0
		pct = 0
		for row in reader:
			row_count = sum(1 for alias in row)
			for alias in row:
				r+=1
				proc = pexpect.spawn("ldapsearch -LLL -x -h ldap -b \"ou=users,ou=moira,dc=mit,dc=edu\" \"uid=" + alias + "\"")
				proc.expect(pexpect.EOF)
				ldap = proc.before
				yind = string.find(ldap,'mitDirStudentYear: ')
				if (yind != -1):
					# Year (G or otherwise)
					year = ldap[yind:]
					yind = string.find(year,'\r\n')
					year = year[19:yind].replace(',','')
					if (year is 'G'):
						# On Campus Status
						ocInd = string.find(ldap,'mitXfinityOnCampusStatus: ')
						if (ocInd==-1):
							oncampus = 'not_listed'
						else:
							oncampus = ldap[ocInd:]
							ocInd = string.find(oncampus,'\r\n')
							oncampus = oncampus[26:ocInd].replace(',','')
					
						# Department
						deptInd = string.find(ldap,'ou: ')
						if (deptInd==-1):
							dept = 'not_listed'
						else:
							dept = ldap[deptInd:]
							deptInd = string.find(dept,'\r\n')
							dept = dept[4:deptInd].replace(',','')
		
						# Given Name
						gnInd = string.find(ldap,'givenName: ')
						if (gnInd==-1):
							gn = 'not_listed'
						else:
							gn = ldap[gnInd:]
							gnInd = string.find(gn,'\r\n')
							gn = gn[11:gnInd].replace(',','')

						# Surname
						snInd = string.find(ldap,'sn: ')
						if (snInd==-1):
							sn = 'not_listed'
						else:
							sn = ldap[snInd:]
							snInd = string.find(sn,'\r\n')
							sn = sn[4:snInd].replace(',','')
						
						# Write Information to File
						writer.writerow({'Surname': sn, 'GivenName': gn, 'Year': year, 'OnCampus': oncampus, 'Department': dept, 'Alias': alias})
						
						# Debug Display
						if (100*r/row_count>=pct+1):
							pct = math.floor(100*r/row_count)
							print(str(pct) + "%", end='\r')
							sys.stdout.flush()
