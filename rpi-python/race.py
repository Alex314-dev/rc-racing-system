from sector import *
from utils import calculate_sectors_times, set_current_and_next_sector_times

import timeit


class Race:
    def __init__(self, flag):
        self.flag = flag #0 means a race, 1 means a challenge
        self.sectors = []
        self.total_time = -1

    def get_flag(self):
        return self.flag

    def get_amount_of_sectors(self):
        return len(self.sectors)

    def get_total_time(self):
        return self.total_time

    def set_total_time(self, time):
        self.total_time = time

    def get_sector_by_id(self, id):
        return self.sectors[id - 1]

    def add_sector(self):
        new_id = len(self.sectors)
        self.sectors.append(Sector(new_id))

    def set_sector_signal_by_id(self, id):
        self.sectors[id - 1].set_signal(1)

    def get_sector_signal_by_id(self, id):
        return self.sectors[id-1].get_signal()

    def set_sector_time_by_id(self, id, time):
        self.sectors[id - 1].set_total_time(time)

    def get_sector_time_by_id(self, id):
        return self.sectors[id-1].get_sector_time()


    def start(self):
        # add the 4 sectors to the race setup
        for x in range(4):
            self.add_sector()

        self.operate_race()

        msg_to_send_back = self.build_msg()
        return msg_to_send_back

    def operate_race(self):
        total_time = -1

        while True:
            start = timeit.default_timer()
            self.get_sector_by_id(1).set_start_time(start)

            if 1 == 1:  # if sector 1 sent back the signal
                set_current_and_next_sector_times(self, 1)

            if 2 == 2:  # if sector 2 sent back the signal
                set_current_and_next_sector_times(self, 2)

            if 3 == 3:  # if sector 3 sent back the signal
                set_current_and_next_sector_times(self, 3)

            if 4 == 4:  # if sector 4 sent back the signal
                self.set_sector_signal_by_id(4)
                current_time = timeit.default_timer()
                self.get_sector_by_id(4).set_end_time(current_time)

                end = timeit.default_timer()

                total_time = end - start

                break

        calculate_sectors_times(self)

        print('Total Time: ', total_time)
        self.set_total_time(total_time)

    def build_msg(self):
        msg = "DONE"
        msg += self.get_flag()
        msg += self.get_total_time()

        for i in range(self.get_amount_of_sectors()):  # in our case, amount_of_sectors = 4
            msg += str(i + 1)
            if self.get_sector_by_id(i + 1).get_signal() == 0:
                msg += "00:00:00"
            msg += str(self.get_sector_by_id(i + 1).get_sector_total_time())

        msg += ";"

        return msg
