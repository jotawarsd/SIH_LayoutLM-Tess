from flask import Flask, request, jsonify
import os
import urllib.request
from werkzeug.utils import secure_filename
from layoutlm_preprocess import *


from utility import layoutlm, ranktext

app = Flask(__name__)


UPLOAD_FOLDER = "C:/Shaunak/SIH/Layoutlm_Tess/app/static/uploads"
app.config["UPLOAD_FOLDER"] = UPLOAD_FOLDER
app.config["MAX_CONTENT_LENGTH"] = 16 * 1024 * 1024

ALLOWED_EXTENSIONS = set(["txt", "pdf", "png", "jpg", "jpeg", "gif"])


def allowed_file(filename):
    return "." in filename and filename.rsplit(".", 1)[1].lower() in ALLOWED_EXTENSIONS


@app.route("/")
def main():
    return "Homepage"


@app.route("/detect", methods=["GET", "POST"])
def detect():
    # check if the post request has the file part
    if "files" not in request.files:
        resp = jsonify({"message": "No file part in the request"})
        resp.status_code = 400
        return resp

    files = request.files.getlist("files")

    errors = {}
    success = False

    for file in files:
        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            file.save(os.path.join(app.config["UPLOAD_FOLDER"], filename))
            success = True
        else:
            errors[file.filename] = "File type is not allowed"

    if success and errors:
        errors["message"] = "File(s) successfully uploaded"
        resp = jsonify(errors)
        resp.status_code = 500
        return resp
    if success:
        out_string = layoutlm("static/uploads/"+filename)
        keywords = ranktext(out_string)
        resp = jsonify({"keywords": keywords ,  "status": 201})
        resp.status_code = 201
        return resp
    else:
        resp = jsonify(errors)
        resp.status_code = 500
        return resp


if __name__ == "__main__":
    app.run(debug=True)