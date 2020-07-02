#!/usr/bin/env python
from __future__ import print_function
import pexpect, csv, string, math, sys, os

INPUT_FN = 'dir.csv'
OUTPUT_FN = 'dir_w.csv'
FIELDS = ['Surname','GivenName','Year','OnCampus','Department','Alias']

# If the output file already exists, read the aliases and append
lastUID = ''
output_exists = False
if os.path.isfile(OUTPUT_FN):
	output_exists = True
	with open(OUTPUT_FN, 'rb') as file:
		reader = csv.DictReader(file)
		for row in reader:
			lastUID = row['Alias']
	print('Starting with alias {}'.format(lastUID))

with open(INPUT_FN, 'rb') as readfile:
	reader = csv.reader(readfile)

	with open(OUTPUT_FN, 'a' if output_exists else 'wb') as writefile:
		writer = csv.DictWriter(writefile, fieldnames=FIELDS)
		if not output_exists:
			writer.writeheader();

		r = 0
		pct = 0
		no_errors = True
		for row in reader:
			num_aliases = sum(1 for alias in row)
			record_data = False if lastUID else True
			for alias in row:
				r+=1
				if record_data:
					try:
						proc = pexpect.spawn("ldapsearch -LLL -x -h ldap -b \"ou=users,ou=moira,dc=mit,dc=edu\" \"uid=" + alias + "\"")
						proc.expect(pexpect.EOF)
						ldap = proc.before
						if "Can't contact" in ldap:
							raise
					except:
						no_errors = False
						break
						
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
							if (100*r/num_aliases>=pct+1):
								pct = math.floor(100*r/num_aliases)
								print(str(pct) + "%", end='\r')
								sys.stdout.flush()
				else:
					record_data = alias == lastUID
					print('Found {}! Beginning scrape where I left off.'.format(lastUID))

# Present data
print('Completed without errors!' if no_errors else 'Connection lost! Please reconnect and start again.')