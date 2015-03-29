#This file parses the bytes file.
import sys
import csv
from os import listdir

def main():
	mypath = "/home/vizkid2005/Desktop/kaggle/"
	allFiles =  listdir(mypath)
	for f in allFiles:
		if ".py" not in f  and "bytes" in f:
			with open(mypath+f,"r") as myfile:
				allLines = myfile.readlines()
				allLines = [line.split(" ") for line in allLines]
				cleanLines(allLines)

def cleanLines(allLines):
	
	for line in allLines:
		line = line[1:]
		lastElem = line[-1]
		lastElem = lastElem[:len(lastElem)-2]
		line[-1] = lastElem
		print line	
		break

if __name__ == "__main__" : main()

