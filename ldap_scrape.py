#!/usr/bin/env python
from __future__ import print_function
import pexpect, csv, math, sys, os

INPUT_FN = 'dir.csv'
OUTPUT_FN = 'dir_w.csv'
FIELDS = ['Surname','GivenName','Year','OnCampus','Department','Alias']

# If the output file already exists, read the aliases and append
lastUID = ''
output_exists = False
if os.path.isfile(OUTPUT_FN):
	output_exists = True
	with open(OUTPUT_FN, 'r') as file:
		reader = csv.DictReader(file)
		for row in reader:
			lastUID = row['Alias']
	print('Starting with alias {}'.format(lastUID))

with open(INPUT_FN, 'r') as readfile:
	reader = csv.reader(readfile)

	with open(OUTPUT_FN, 'a' if output_exists else 'w') as writefile:
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
						ldap = proc.before.decode('ascii')
						if "Can't contact" in ldap:
							raise
					except:
						no_errors = False
						print(alias)
						break
						
					yind = ldap.find('mitDirStudentYear: ')
					if (yind != -1):
						# Year (G or otherwise)
						year = ldap[yind:]
						yind = year.find('\r\n')
						year = year[19:yind].replace(',','')
						if (year is 'G'):
							# On Campus Status
							ocInd = ldap.find('mitXfinityOnCampusStatus: ')
							if (ocInd==-1):
								oncampus = 'not_listed'
							else:
								oncampus = ldap[ocInd:]
								ocInd = oncampus.find('\r\n')
								oncampus = oncampus[26:ocInd].replace(',','')
						
							# Department
							deptInd = ldap.find('ou: ')
							if (deptInd==-1):
								dept = 'not_listed'
							else:
								dept = ldap[deptInd:]
								deptInd = dept.find('\r\n')
								dept = dept[4:deptInd].replace(',','')
			
							# Given Name
							gnInd = ldap.find('givenName: ')
							if (gnInd==-1):
								gn = 'not_listed'
							else:
								gn = ldap[gnInd:]
								gnInd = gn.find('\r\n')
								gn = gn[11:gnInd].replace(',','')

							# Surname
							snInd = ldap.find('sn: ')
							if (snInd==-1):
								sn = 'not_listed'
							else:
								sn = ldap[snInd:]
								snInd = sn.find('\r\n')
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
					if record_data: print('Found {}! Beginning scrape where I left off.'.format(lastUID))

# Present data
print('Completed without errors!' if no_errors else 'Connection lost! Please reconnect and start again.')