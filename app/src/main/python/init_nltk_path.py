import os
import nltk

print(f"Current working directory: {os.getcwd()}")

def initPath(files_dir):
    # Add the path to NLTK's data search paths
    if files_dir not in nltk.data.path:
        nltk.data.path.append(files_dir)
        print(f"Added {files_dir} to NLTK data paths.")
    else:
        print(f"{files_dir} is already in NLTK data paths.")


    print(f"NLTK data directory: {nltk.data.path}")
