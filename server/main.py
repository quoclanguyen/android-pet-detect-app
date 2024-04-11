from flask import Flask
from flask_mail import Mail, Message
from dotenv import load_dotenv
import os

load_dotenv()

app = Flask(__name__)
mail = Mail(app)

@app.route("/")
def page():
    return "Hello"




if __name__ == '__main__':
    app.run(host = '0.0.0.0', port = 8080)