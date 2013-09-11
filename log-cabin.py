from tables import *
 
## we define the device we use for EEG recording 
class Neurosky(IsDescription):
    time = Time64() 
    alpha1 = Int32Col()     
    alpha2 = Int32Col()
    beta1 = Int32Col()
    beta2 = Int32Col()
    delta1 = Int32Col()
    delta2 = Int32Col()
    gamma = Int32Col()
    theta = Int32Col()
    error_rate = Int32Col()


## let's make an example database
filename = "test.h5"
# we title this file with the unique ID of the device we're using - below string is a stupid example
# the idea here is to keep data from different recording devices separate - apples to oranges
h5file = open_file(filename, mode = "w", title = "nicks-neurosky")
# Create a new group in the db's root and name it with our participant's unique ID (again, a stupid example instead of a proper hash)
# This way, it's not too hard to draw data from other users of the same device in case this information is relevant
# (if data from two different devices is apples to oranges, then inter-user comparisons within the same device are gala apples to granny smiths eh...)
group = h5file.create_group("/", 'detector', 'Lord Donus')
# Create one table on it and name the table after whatever macro we're recording
# eventually the user will pick this macro name through our convenient graphical interface........someday......
table = h5file.create_table(group, 'readout', Neurosky, "Listening to Burial")

## fill a table with our sample text file
reading = table.row
# load the test file for reading

log = open('my-great-neurolog.txt','r')
for entry in log:
    line = entry.split(',')

    #reading['time'] = line[0] 
    # in the current example file, times are NOT stored in unix time... 
    # so, we will have to convert them before we can save the time at all

    reading['alpha1'] = line[1]
    reading['alpha2'] = line[2]
    reading['beta1'] = line[3]
    reading['beta2'] = line[4]
    reading['delta1'] = line[5]
    reading['delta2'] = line[6]
    reading['gamma'] = line[7]
    reading['theta'] = line[8]
    reading['error_rate'] = line[9]
    reading.append()

h5file.close()