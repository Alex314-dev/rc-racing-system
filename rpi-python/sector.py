class Sector:
    def __init__(self, id):
        self.signal = 0
        self.id = id
        self.start_time = 0
        self.end_time = 0
        self.total_time = 0

    def get_signal(self):
        return self.signal

    def set_signal(self, signal):
        self.signal = signal

    def get_start_time(self):
        return self.start_time

    def set_start_time(self, time):
        self.start_time = time

    def get_end_time(self):
        return self.end_time

    def set_end_time(self, time):
        self.end_time = time

    def get_sector_total_time(self):
        return self.total_time

    def calculate_total_time(self):
        self.total_time = self.end_time - self.start_time

