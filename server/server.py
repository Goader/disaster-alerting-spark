from model import Model

import colorama
from argparse import ArgumentParser
from flask import Flask, jsonify, request


app = Flask(__name__)
model = None


@app.route('/')
@app.route('/index', methods=['GET'])
def index():
    return 'Disaster Alerting System Server is running'


@app.route('/predict', methods=['POST'])
def predict():
    data = request.get_json()

    cls = model.predict(data['text'])
    
    if cls == 'not disaster':
        print(f'  {colorama.Fore.GREEN}{data["text"]}{colorama.Style.RESET_ALL}')
    else:
        print(f'  {colorama.Fore.RED}{data["text"]}{colorama.Style.RESET_ALL}')

    return jsonify({
        'class': cls
    }), 200


if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('model_path', help='a path to the Flair TextClassifier model')
    
    args = parser.parse_args()

    model = Model(args.model_path)

    app.run()
