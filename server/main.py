from smtplib import SMTPServerDisconnected
from flask import Flask, request
from flask_mail import Mail, Message
from dotenv import load_dotenv
from datetime import datetime as dt
from concurrent.futures import ThreadPoolExecutor
from time import sleep
import os
import random
import hashlib
app = Flask(__name__)

load_dotenv(dotenv_path="../.env")

app.config['MAIL_SERVER'] = os.getenv("SMTP_HOST")
app.config['MAIL_PORT'] = os.getenv("SMTP_PORT")
app.config['MAIL_USERNAME'] = os.getenv("SMTP_USER")
app.config['MAIL_PASSWORD'] = os.getenv("SMTP_PASSWORD")
app.config['MAIL_USE_TLS'] = False
app.config['MAIL_USE_SSL'] = True

mail = Mail(app)

executor = ThreadPoolExecutor(2)
OTP_TTL = 40 # 3 minutes
accounts = {
    # "userToken": {"password":hashed, "OTP":6 digits string, "OTPtimestamp":timestamp, "registered":True/False}
}

def OTPAutoDestroy(token):
    sleep(OTP_TTL)
    if (accounts[token]["registered"] == False):
        accounts.pop(token)
        print(accounts)
        return
    print(accounts)
    

@app.route("/login", methods = ['POST'])
def login():
    res = request.json
    print(res)
    email = res.get('email')
    passw = res.get('password')
    
    m = hashlib.sha256()
    m.update(str(email).encode("utf-8"))
    hexToken = m.hexdigest()
    accCreated = accounts.get(hexToken, "null")
    if accCreated == "null":
        return "Account does not exist", 404
    print(accounts)
    if accounts[hexToken]['registered'] == False:
        return "Account does not exist", 404
    print(passw)
    if passw != accounts[hexToken]['password']:
        return "Wrong password", 401
    
    return "Login success", 200

@app.route("/register", methods = ['POST'])
def register():
    global otpThread
    res = request.json
    print(res)
    email = res.get('email')
    passw = res.get('password')
    otp = "".join((random.sample("0 1 2 3 4 5 6 7 8 9".split(), counts = [100]*10, k = 6)))
    
    m = hashlib.sha256()
    m.update(str(email).encode("utf-8"))
    hexToken = m.hexdigest()
    try:
        if accounts[hexToken]['registered']:
            return "Email used", 409
    except:
        pass
    newDict = {}
    newDict["password"] = passw
    newDict["OTP"] = otp
    timeStamp = dt.timestamp(dt.now())
    newDict["OTPdatestamp"] = timeStamp
    newDict["registered"] = False
    accounts[hexToken] = newDict



    executor.submit(OTPAutoDestroy, hexToken)
    # otpThread = thr.Thread(target = OTPAutoDestroy, args=(timeStamp, hexToken))
    print(accounts)
    while True:
        try:
            msg = Message('Your Petreco Registration Code', sender = ("Petreco", "peter@mailtrap.io"), recipients = [email])
            msg.html = \
            f"Your registration code: <b>{otp}</b>"
            \
            mail.send(msg)
            break
        except:
            raise SMTPServerDisconnected("Connection unexpectedly closed")
    
    return "Wait for OTP", 200

@app.route("/registerConfirm", methods = ['POST'])
def registerConfirm():
    res = request.json
    email = res.get('email')
    otp = res.get('otp')
    
    m = hashlib.sha256()
    m.update(str(email).encode("utf-8"))
    hexToken = m.hexdigest()

    OTPalive = accounts.get(hexToken, False)

    if OTPalive == False:
        return "OTP expired", 409
    if accounts[hexToken]["OTP"] != otp:
        return "Wrong OTP", 409
    accounts[hexToken]["registered"] = True
    print(accounts)
    while True:
        try:
            msg = Message('Register success', sender = ("Petreco", "peter@mailtrap.io"), recipients = [email])
            msg.html = \
            f"Your registration was successful. You can login with your account"
            \
            mail.send(msg)
            break
        except:
            raise SMTPServerDisconnected("Connection unexpectedly closed")
    return "Register success", 200

if __name__ == '__main__':
    app.run(host = '192.168.1.4', port = 8080, debug=True)