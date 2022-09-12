from layoutlm_preprocess import *
import pytesseract
import PIL

import nltk
from nltk import word_tokenize
import string

from nltk.stem import WordNetLemmatizer

from torch.nn import CrossEntropyLoss

import numpy as np
import math
from keybert import KeyBERT
import spacy

class ModelLoader :
    def __init__():
        labels = get_labels("labels.txt")
        model=model_load('layoutlm.pt', len(labels))

def get_labels(path):
    with open(path, "r") as f:
        labels = f.read().splitlines()

    return labels

def iob_to_label(label):
    if label != 'O':
      return label[2:]
    else:
      return ""

def clean(text):
    text = text.lower()
    printable = set(string.printable)
    text = filter(lambda x: x in printable, text)
    text = "".join(list(text))
    return text

def layoutlm(image_path):
    image = Image.open(image_path)
    image = image.convert("RGB")

    labels = get_labels("labels.txt")
    num_labels = len(labels)
    label_map = {i: label for i, label in enumerate(labels)}
    # Use cross entropy ignore index as padding label id so that only real label ids contribute to the loss later
    pad_token_label_id = CrossEntropyLoss().ignore_index

    model_path='layoutlm.pt'
    model=model_load(model_path, num_labels)
    image, words, boxes, actual_boxes = preprocess(image_path)

    word_level_predictions, final_boxes, token_type_ids=convert_to_features(image, words, boxes, actual_boxes, model)

    draw = ImageDraw.Draw(image)
    font = ImageFont.load_default()

    label2color = {'question':'blue', 'answer':'green', 'header':'orange', '':'violet'}
    for prediction, box, wrd in zip(word_level_predictions, final_boxes, words):
        predicted_label = iob_to_label(label_map[prediction]).lower()
        draw.rectangle(box, outline=label2color[predicted_label])
        draw.text((box[0] + 10, box[1] - 10), text=predicted_label, fill=label2color[predicted_label], font=font)

    label_list = []

    for token in word_level_predictions:
        label_list.append(label_map[token])

    wt_pairs = {}

    for w, t in zip(label_list, words):
        wt_pairs[t] = w
    # # #print(wt_pairs)

    out_string = ""

    for i in words:
        out_string += i + ' '
    print(out_string)
    return out_string

def ranktext(Text):
    kw_model = KeyBERT()
    keywords = kw_model.extract_keywords(Text, keyphrase_ngram_range=(0, 1), stop_words='english',
                              use_mmr=True, diversity=0.9)
    lst = []

    for i in keywords:
        lst.append(i[0])

    return lst


