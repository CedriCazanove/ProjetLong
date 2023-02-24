import numpy as np
import cv2
import matplotlib.pyplot as plt
from PIL import Image
import io
import base64

def main(X, Y):
    fig = plt.figure()

    x = X.split(",") #suppose X = "1,2,3"
    y = Y.split(",")

    xData = []
    yData = []

    for i in x:
        xData.append(float(i))
    for i in y:
        yData.append(float(i))

    fig = plt.figure()
    plt.xlabel("Axe transversal (m/s²)")
    plt.ylabel("Axe antéro-postérieur (m/s²)")
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

