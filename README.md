# CS321-F19-BTree

BTree Project - Cody Gardner, Kenton Goldthorpe, Sammie Fullmer

This program is intended to take in a gbk file and break down the a-c-g-t data into sequences of specified length follwoing a sliding window strategy and then inserts each sequence of data into a BTree. After a BTree of data has been built, a second driver class can be used to search for specific sequences in the generated BTree.

First driver class is the GeneBankCreateBTree class. Expected useage is as follows:
    java GeneBankCreateBTree <0/1(no/with cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]

    1st argument: <0/1(no/with cache)> - 0 to build BTree without a cache, 1 to build BTree with a     cache.
    2nd argument: <degree> - >= 0, specifies the degree for the BTree which determines how many        objects can be stored in a node and therefore how many child nodes each node has as well. If  0 or 1 - the program uses the optimal degree for the most efficient read and write process.
    3rd argument: <gbk file> - the data file that contains the a-c-g-t data string/s that data sequences will be generated from, must be a gbk file.
    4th argument: <sequence length> - 1-31, specifies the length of the the data string sequence for each data object generated from the a-c-g-t string.
    5th argument: [<cache size>] - if the BTree is built using a cache, this specifies the size of the cache to be used.
    6th argumen: [<debug level>] - 0 or 1, specifies the output of the BTree generation. 0 will only print out error and help messages if any arrise. 1 will generate a file named "dump" of the BTree printed in order.

BTree Data Layout:
This program builds a BTree data structure by reading and writing data to and from a file. The file data is written in a specific order. The organization structure is as follows:
    BTree MetaData - first 21 bytes
        Degree (int) - 4 bytes
        Sequence Length (int) - 4 bytes
        Cache Useage (boolean) - 1 byte
        Next Node Address for a new node (int) - 4 bytes
        Address of Root Node (int) - 4 bytes
        Node Size (int) - 4 bytes
    
    All data following the first 21 bytes is the data of the BTreeNodes. Each BTreeNode is organized and written to the file in the following order:
        Leaf Node (boolean) - 1 byte
        MetaDataSize (int) - 4 bytes
        Node Size (int) - 4 bytes
        Number of Objects in Node (int) - 4 bytes
        Address/Offset in file (int) - 4 bytes
        Parent Node Address/Offset in file (int) - 4 bytes
        Degree (int) - 4 bytes
        Array of Child Node Pointers (ints) - Size of the Child Pointer array is 2*Degree
            Each Child Node Pointer is an int value of the address/offset in the file of the child node
        Array of TreeObjects - Size of the TreeObject array is 2*Degree - 1
            Each TreeObject is written is a specific order as well
            Data Value (long) - 8 bytes
            Frequency (int) - 4 bytes
            Sequence Length (int) - 4 bytes

Second driver class is the GeneBankSearch class. Expected useage is as follows:
    java GeneBankSearch <0/1(no/with cache)> <btree file> <query file> [<cache size>] [<debug level>]

    1st argument: <0/1(no/with cache)> - specifies to search with or without a cache.
    2nd argument: <btree file> - specifies the btree file which was generated when the btree was built. This file contains the btree data that will be searched through.
    3rd argument: <query file> - specifies the query file which contains the data sequences to search for in the btree.
    4th argument: [<cache size>] - if a cache is to be used, this specifies the size of the cache.
    5th argument: [<debug level>] - 0 value here will print out the results of the queries.

General Comments:
The cache functionality works well for GeneBankSearch, but it doesn't work quite right for GeneBankCreateBTree. When building a BTree with a cache, all of the data sequences are found correctly and inserted into the BTree, but the frequencies aren't quite right. After writing out the specific layout of the BTree data as it is written to the file, I can see that there may be some redundant data in the BTree nodes and it is likely that this program can be greatly simplified and conserve data space on the file. I also think that this over complication could be a part of the issue with the cache useage when building a btree. Since that cache functionality is not correct, we were not able to compare run times between using a cache and not.