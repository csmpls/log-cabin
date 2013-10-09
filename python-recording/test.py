import mindwave, time
from threading import Thread
import datetime
import Queue


class MindWaveMonitor(Thread):
    def __init__(self, out_queue):
        Thread.__init__(self)
        self.out_queue = out_queue
        self.headset = mindwave.Headset('/dev/tty.MindWave', 'C61A')
        self.headset.connect()
        self._stop = False
        time.sleep(2)

    def run(self):
        print "Attempting to connect with MindWave headset."
        while self.headset.status != 'connected':
            time.sleep(0.5)
            if self.headset.status == 'standby':
                self.headset.connect()
        print "Connected."

        while not self._stop:
            time.sleep(1)
            self.out_queue.put(EEGEvent(datetime.datetime.now(), self.headset.attention, self.headset.meditation))
        
        return

class InteractionEvent:
    def __init__(self, time):
        self.time = time

class EEGEvent(InteractionEvent):
    def __init__(self, time, med, att):
        InteractionEvent.__init__(self, time)
        self.med = med
        self.att = att


event_queue = Queue.Queue()

headset = mindwave.Headset('/dev/tty.MindWave', 'C61A')
def mindwaveHandler(set, data):
    print "EEG (time, data): ", datetime.datetime.now(), " , ", data

headset.power_val_handlers.append(mindwaveHandler)


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
