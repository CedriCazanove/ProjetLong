import numpy as np
from scipy import signal
from scipy.integrate import cumtrapz
import cv2
import matplotlib.pyplot as plt
from PIL import Image
import io
import base64

def main(X, Y):
    x = X.split(",") #suppose X = "1,2,3"
    y = Y.split(",")

    xData = []
    yData = []

    for i in x:
        xData.append(float(i))
    for i in y:
        yData.append(float(i))
        
    # Plot the figure of the Raw Data collected from the Accelerometer of the phone
    fig = plt.figure()
    plt.xlabel("Axe transversal (m/s²)")
    plt.ylabel("Axe antéro-postérieur (m/s²)")
    plt.title('Accélération du téléphone')
    plt.plot(xData, yData, linewidth=1, color='red')

    fig.canvas.draw() #now we use this canvas to convert data to numpy array..

    img = np.fromstring(fig.canvas.tostring_rgb(), dtype=np.uint8, sep='')
    img = img.reshape(fig.canvas.get_width_height() [::-1]+(3,)) #reshape data
    img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)

    pil_im = Image.fromarray(img)
    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64encode(buff.getvalue())

    return ""+str(img_str,'utf-8')

def position(X, Y, rate):
    x = X.split(",") #suppose X = "1,2,3"
    y = Y.split(",")

    aX = []
    aZ = []

    for i in x:
        aX.append(float(i))
    for i in y:
        aZ.append(float(i))

    # Calculate the acquisition time step
    dt = float(1 / float(rate))
    ################ Double integration ####################
    
    # Double integrate the Accelerometer Data to get the position 
    # We initialize the speed and the position with 0 
    Data_x =cumtrapz(cumtrapz(aX,dx=dt),dx=dt)
    Data_z =cumtrapz(cumtrapz(aZ,dx=dt),dx=dt)

    ############### Filtre Butterworth ####################
   
    # cut frequencies for the filter 
    fc1 = 0.1  # Hz
    fc2 = 20   # Hz

    # Nyquist frequence
    f_nyq = 1 /( 2.*(dt))  # Hz
    
    # Preparing the band pass filter for X, Y and Z
    b, a = signal.butter(2, (fc1/f_nyq, fc2/f_nyq), 'bandpass', analog=False)
    
    # We apply the filter 
    Data_x = signal.filtfilt(b, a, Data_x)
    Data_z = signal.filtfilt(b, a, Data_z)

    # Plot the results 2D of X and Z axis
    fig = plt.figure()
    plt.plot(Data_x,Data_z, linewidth=1, color='r')
    plt.title('Trajectoire du téléphone 2D')
    plt.xlabel("Axe transversal (m)")
    plt.ylabel("Axe antéro-postérieur (m)")
    plt.plot(Data_x, Data_z, linewidth=1, color='red')

    fig.canvas.draw() #now we use this canvas to convert data to numpy array..

    img = np.fromstring(fig.canvas.tostring_rgb(), dtype=np.uint8, sep='')
    img = img.reshape(fig.canvas.get_width_height() [::-1]+(3,)) #reshape data
    img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)

    pil_im = Image.fromarray(img)
    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64encode(buff.getvalue())

    return ""+str(img_str,'utf-8')
