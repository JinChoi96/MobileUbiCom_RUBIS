import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.legend_handler import HandlerPatch
from matplotlib.animation import FuncAnimation
from itertools import count

#data = pd. read_csv("./dataset.csv")

#subset_vehicle = data[['Vehicle_GPS_X', 'Vehicle_GPS_Y']]

#print(subset_vehicle)


#circle_center_x = subset_vehicle.iloc[0].Vehicle_GPS_X
#circle_center_y = subset_vehicle.iloc[0].Vehicle_GPS_Y



#ax = plt.subplot(111, aspect=1)

'''
x_vehicle = data['Vehicle_GPS_X']
y_vehicle = data['Vehicle_GPS_Y']

x_user = data['User_GPS_X']
y_user = data['User_GPS_Y']

vehicle_x, = ax.plot(x_vehicle, y_vehicle, "k+", mew=5, ms=5)
vehicle_y, = ax.plot(x_vehicle, y_vehicle, "w+", mew=1, ms=1)

user_x, = ax.plot(x_user, y_user, "k+", mew=3, ms=12)
user_y, = ax.plot(x_user, y_user, "w+", mew=1, ms=10)

c = mpatches.Circle((subset_vehicle.iloc[0].Vehicle_GPS_X, subset_vehicle.iloc[0].Vehicle_GPS_Y), 0.001, fc="g", ec="r", lw=3)
ax.add_patch(c)

index = count()
'''
data = pd.read_csv("./dataset.csv")


subset_vehicle = data[['Vehicle_GPS_X', 'Vehicle_GPS_Y']]
subset_user = data[['User_GPS_X', 'User_GPS_Y']]

print(subset_vehicle)

def animation_frame(i):
    #data = pd.read_csv("./dataset.csv")
    #subset_vehicle = data[['Vehicle_GPS_X', 'Vehicle_GPS_Y']]
    #subset_user = data[['User_GPS_X', 'User_GPS_Y']]

    #print(subset_vehicle)

    #circle_center_x = subset_vehicle.iloc[i-1].Vehicle_GPS_X
    #circle_center_y = subset_vehicle.iloc[i-1].Vehicle_GPS_Y

    ax = plt.subplot(111, aspect=1)

    x_vehicle = subset_vehicle.iloc[i].Vehicle_GPS_X
    y_vehicle = subset_vehicle.iloc[i].Vehicle_GPS_Y

    x_user = subset_user.iloc[i].User_GPS_X
    y_user = subset_user.iloc[i].User_GPS_Y

    plt.cla()

    vehicle_x, = ax.plot(x_vehicle, y_vehicle, "k+", mew=5, ms=5)
    vehicle_y, = ax.plot(x_vehicle, y_vehicle, "w+", mew=1, ms=1)

    user_x, = ax.plot(x_user, y_user, "k+", mew=3, ms=12)
    user_y, = ax.plot(x_user, y_user, "w+", mew=1, ms=10)

    ax.set_xlim([37.44, 37.46])
    ax.set_ylim([126.94, 126.96])

    c = mpatches.Circle((subset_vehicle.iloc[i].Vehicle_GPS_X, subset_vehicle.iloc[i].Vehicle_GPS_Y), 0.0041, fc="g",
                        ec="r", lw=3)
    ax.add_patch(c)

    plt.tight_layout()

    def make_legend_circle(legend, orig_handle, xdescent, ydescent, width, height, fontsize):
        p = mpatches.Circle(xy=(0.5 * width - 0.5 * xdescent, 0.5 * height - 0.5 * ydescent), radius=5)

        return p

    plt.legend([c, (vehicle_x, vehicle_y), (user_x, user_y)], ["Alertion Boundary", "Vehicle", "User"],
               handler_map={mpatches.Circle: HandlerPatch(patch_func=make_legend_circle), })



animation = FuncAnimation(plt.gcf(), animation_frame, interval=1000)

'''
def make_legend_circle(legend, orig_handle, xdescent, ydescent, width, height, fontsize):
    p = mpatches.Circle(xy=(0.5 * width - 0.5 * xdescent, 0.5 * height - 0.5 * ydescent), radius=5)

    return p


plt.legend([c, (vehicle_x, vehicle_y), (user_x, user_y)], ["Alertion Boundary", "Vehicle", "User"],
           handler_map={mpatches.Circle: HandlerPatch(patch_func=make_legend_circle), })
'''

plt.show()



