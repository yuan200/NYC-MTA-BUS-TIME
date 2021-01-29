import json

with open("manhattan_stop.json") as f:
    data = json.load(f)

print(data)