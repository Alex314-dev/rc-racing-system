def calculate_sectors_times(race):
    for i in range(4):
        race.get_sector_by_id(i+1).calculate_total_time()


def set_current_and_next_sector_times(race, current_sector):
    race.set_sector_signal_by_id(current_sector)
    current_time = timeit.default_timer()
    race.get_sector_by_id(current_sector).set_end_time(current_time)

    race.get_sector_by_id(current_sector + 1).set_start_time(current_time)