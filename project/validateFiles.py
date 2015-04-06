import sys
from csv import reader
import os

path = "/N/dc2/scratch/hydargah/dm/trainRaw/"

filePath = "/N/dc2/scratch/hydargah/dm/trainLabels.csv"
bigDict = {}

def main():
	byteFileNames = [f for f in os.listdir(path) if ".bytes" in f]
	asmFileNames = [f for f in os.listdir(path) if ".asm" in f]
	with open(filePath) as f:
		f.readline()
		csv = reader(f)
		for line in csv:
			bigDict[line[0]] = 'empty'
	
	for name in byteFileNames:
		if name in bigDict:
			bigDict[name] = 'byte'

	for name in asmFileNames:
		if name in bigDict:
			if bigDict[name] == 'byte' :
				bigDict[name] = 'both'
			else:
				bigDict[name] = 'asm'		

	missingFiles = []
	missingAsmFiles = []
	missingByteFiles = []
	for key in bigDict:
		if bigDict[key] == 'empty':
			missingFiles.extend(key)

		if bigDict[key] == 'asm':
			missingByteFiles.extend(key)

		if bigDict[key] == 'byte':
			missingAsmFiles.extend(key)
	print bigDict
	print "Total asm files missing : "+str(len(missingAsmFiles))
	print "Total byte files missing : "+str(len(missingByteFiles))
	print "Both files missing :"+str(len(missingFiles))

if __name__ == "__main__":main()
