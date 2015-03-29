#This file parses the bytes file.
import sys
import csv
import os

allTokens = {}

def main():
	mypath = os.getcwd()+"/"+"bytefiles/"
	allFiles =  os.listdir(mypath)
	for f in allFiles:
		if ".py" not in f  and "bytes" in f:
			with open(mypath+"/"+f,"r") as myfile:
				allLines = myfile.readlines()
				allLines = [line.split(" ") for line in allLines]
				cleanLines(allLines)

	print len(allTokens.keys())
				
def cleanLines(allLines):
	cleanedLines = []

	for line in allLines:
		line = line[1:]
		lastElem = line[-1]
		lastElem = lastElem[:len(lastElem)-2]
		line[-1] = lastElem
		cleanedLines.append(line)
		for byte in line:
			if byte not in allTokens:
				allTokens[byte] = 1
			else:
				value = allTokens[byte]
				allTokens[byte] = value + 1	
		#print line
		
if __name__ == "__main__" : main()

