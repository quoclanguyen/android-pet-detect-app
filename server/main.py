from smtplib import SMTPServerDisconnected
from flask import Flask, request, render_template
from flask_mail import Mail, Message
from dotenv import load_dotenv
from datetime import datetime as dt
from concurrent.futures import ThreadPoolExecutor
from time import sleep
import xml.etree.ElementTree as ET
import os
import random
import hashlib
import socket

hostname = socket.gethostname()
IP_ADDRESS = socket.gethostbyname(hostname) 

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
OTP_TTL = 60*3 # 3 minutes
resetToken_TTL = 60*3
accounts = {
    # "userToken": {"password":hashed, "OTP":6 digits string, "OTPtimestamp":timestamp, "registered":True/False}
    # debug account, 'test'
    '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08': {
        'password': '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', # 1234
        "registered": True
    }
}

def OTPAutoDestroy(token):
    sleep(OTP_TTL)
    if (accounts[token]["registered"] == False):
        accounts.pop(token)
        print(accounts)
        return
    print(accounts)

def resetTokenAutoDestroy(token):
    sleep(resetToken_TTL)
    accounts[token]["resetToken"] = "none"
    print(accounts)

@app.route("/forgotPassword", methods = ['POST'])
def forgotPassword():
    res = request.json
    print(res)
    email = res.get('email')
    m = hashlib.sha256()
    m.update(str(email).encode("utf-8"))
    hexToken = m.hexdigest()
    accCreated = accounts.get(hexToken, "null")
    if accCreated == "null":
        return "Account does not exist", 404
    print(accounts)
    if accounts[hexToken]['registered'] == False:
        return "Account does not exist", 404
    seed = "".join((random.sample("q w e r t y u i o p a s d f g h j k l z x c v b n m 1 2 3 4 5 6 7 8 9 0".split(), counts = [100]*36, k = 20)))
    n = hashlib.sha256()
    n.update(seed.encode('utf-8'))
    token = n.hexdigest()
    url = f"http://{IP_ADDRESS}:8080/resetPassword?resetToken={token}&accToken={hexToken}"
    accounts[hexToken]['resetToken'] = token
    print(url)
    while True:
        try:
            msg = Message('Reset your Petreco password', sender = ("Petreco", "peter@mailtrap.io"), recipients = [email])
            msg.html = \
            f"<div>Hi, {email.split('@')[0]} </div> <div>Let's reset your password so you can continue to use our app to detect cats and dogs breed.</div> <a href=\"" +url+"\">Click here to reset your password</a> <div> <b>If you did not ask to reset your password you may want to review your recent account access for any unusual activity.</b></div>"
            \
            mail.send(msg)
            break
        except:
            raise SMTPServerDisconnected("Connection unexpectedly closed")
            
    return "Sent reset mail", 200

@app.route("/resetPassword", methods = ['GET'])
def resetPassword():
    resetToken = request.args.get('resetToken')
    emailToken = request.args.get('accToken')
    print(resetToken)
    print(emailToken)
    checkAcc = accounts.get(emailToken, 'none')
    if checkAcc == 'none':
        return "Not found", 404
    executor.submit(resetTokenAutoDestroy, emailToken)
    token = accounts[emailToken].get('resetToken', 'none')
    if token != resetToken or token == 'none':
        return "Not authorized", 403
    return render_template('reset.html')

@app.route("/changePassword", methods = ['GET', 'POST'])
def changePassword():
    # Reset password
    isReset = request.args.get('isReset')
    if isReset == '1':
        resetToken = request.args.get('resetToken')
        emailToken = request.args.get('accToken')
        if accounts.get(emailToken, 'none') == 'none':
            return 'Not found', 404
        token = accounts[emailToken].get('resetToken', 'none')
        newPassword = request.args.get('newPassword')
        if token != resetToken:
            return "Not authorized", 403
        accounts[emailToken]['password'] = newPassword
        accounts[emailToken]['resetToken'] = 'none'
        return 'Reset password success', 200
    # Change password
    res = request.json
    print(res)
    email = res.get('email')
    oldPassw = res.get('currentPassword')
    m = hashlib.sha256()
    m.update(str(email).encode("utf-8"))
    emailToken = m.hexdigest()
    if accounts.get(emailToken, 'none') == 'none':
        return 'Not found', 404
    if oldPassw != accounts[emailToken]['password']:
        return "Wrong password", 401
    
    newPassw = res.get('newPassword')
    accounts[emailToken]['password'] = newPassw
    accounts[emailToken]['resetToken'] = 'none'
    return 'Change password success', 200
    


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
    
    xmlPath = "..\\app\\src\\main\\res\\values\\strings.xml"
    
    tree = ET.parse(xmlPath)
    root = tree.getroot()
    
    for string_element in root.iter('string'):
        if string_element.attrib.get('name') == 'base_url':
            string_element.text = f'http://{IP_ADDRESS}:8080/'
    
    tree.write(xmlPath)
    app.run(host = IP_ADDRESS, port = 8080, debug=True)