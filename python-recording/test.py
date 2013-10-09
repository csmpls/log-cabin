

# set up a log 
    
headset = mindwave.Headset('/dev/tty.MindWave', 'C61A')
def mindwaveLogger(set, data):
    with open('eeg-log.csv','a') as f:
	f.write(datetime.datetime.now() + ', ' + data)

headset.power_val_handlers.append(mindwaveLogger)


time.sleep(2)
headset.connect()
while headset.status != 'connected':
    time.sleep(0.5)
    if headset.status == 'standby':
        headset.connect()
        print "Retrying connect..."

print "Connected."

i = 0
while True:
    time.sleep(0.3)
    i = i + 1
    if i > 1000:
        break

headset.disconnect()
