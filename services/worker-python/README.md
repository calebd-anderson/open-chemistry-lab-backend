# [FastAPI](https://fastapi.tiangolo.com/) Worker Service

[![Powered by RDKit](https://img.shields.io/badge/Powered%20by-RDKit-3838ff.svg?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQBAMAAADt3eJSAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAFVBMVEXc3NwUFP8UPP9kZP+MjP+0tP////9ZXZotAAAAAXRSTlMAQObYZgAAAAFiS0dEBmFmuH0AAAAHdElNRQfmAwsPGi+MyC9RAAAAQElEQVQI12NgQABGQUEBMENISUkRLKBsbGwEEhIyBgJFsICLC0iIUdnExcUZwnANQWfApKCK4doRBsKtQFgKAQC5Ww1JEHSEkAAAACV0RVh0ZGF0ZTpjcmVhdGUAMjAyMi0wMy0xMVQxNToyNjo0NyswMDowMDzr2J4AAAAldEVYdGRhdGU6bW9kaWZ5ADIwMjItMDMtMTFUMTU6MjY6NDcrMDA6MDBNtmAiAAAAAElFTkSuQmCC)](https://www.rdkit.org/)

## Development
- The build system is [uv](https://docs.astral.sh/uv/).

### Run Dev
```sh
uv run fastapi dev
```
http://localhost:8000/

### Test
```sh
uv run pytest
```

Add a dev dependency:
```sh
uv add --dev httpx2
```

## CI/CD
### [Docker](https://fastapi.tiangolo.com/deployment/docker)
Export requirements if `project.toml` or `uv.lock` changes
```sh
uv export --format requirements-txt --no-dev --no-emit-project --output-file requirements.txt
```
#### Manually build the image:
```sh
docker build -t worker .
# run the container based on the imate
docker run -d --name fastapi-worker -p 80:80 worker
```
```sh
uv lock --check
uv run ruff check .
uv run ruff format --check .
uv run pytest
```
