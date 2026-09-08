
FROM python:3.14
WORKDIR /code
COPY ./requirements.txt /code/requirements.txt
RUN pip install --no-cache-dir --upgrade -r /code/requirements.txt
COPY ./src /code/app
CMD ["fastapi", "run", "app/worker_python/main.py", "--proxy-headers", "--port", "80"]