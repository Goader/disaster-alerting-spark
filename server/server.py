from model import Model

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
    print(data)

    cls = model.predict(data['text'])
    
    return jsonify({
        'class': cls
    }), 200


if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('model_path', help='a path to the Flair TextClassifier model')
    
    args = parser.parse_args()

    model = Model(args.model_path)

    app.run()
