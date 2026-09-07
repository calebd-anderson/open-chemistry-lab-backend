# [FastAPI](https://fastapi.tiangolo.com/) Worker Service

## [RDKit](https://pypi.org/project/rdkit/)
[![Powered by RDKit](https://img.shields.io/badge/Powered%20by-RDKit-3838ff.svg?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQBAMAAADt3eJSAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAFVBMVEXc3NwUFP8UPP9kZP+MjP+0tP////9ZXZotAAAAAXRSTlMAQObYZgAAAAFiS0dEBmFmuH0AAAAHdElNRQfmAwsPGi+MyC9RAAAAQElEQVQI12NgQABGQUEBMENISUkRLKBsbGwEEhIyBgJFsICLC0iIUdnExcUZwnANQWfApKCK4doRBsKtQFgKAQC5Ww1JEHSEkAAAACV0RVh0ZGF0ZTpjcmVhdGUAMjAyMi0wMy0xMVQxNToyNjo0NyswMDowMDzr2J4AAAAldEVYdGRhdGU6bW9kaWZ5ADIwMjItMDMtMTFUMTU6MjY6NDcrMDA6MDBNtmAiAAAAAElFTkSuQmCC)](https://www.rdkit.org/)

## Development
- The build system is [uv](https://docs.astral.sh/uv/).
- Add dev dependency:
```sh
uv add --dev httpx2
```

### Run Dev
```sh
uv run fastapi dev
```

### Test
```sh
uv run pytest
```

## CI
### Docker
https://fastapi.tiangolo.com/deployment/docker
```sh
# export requirements if `project.toml` or `uv.lock` changes
uv export --format requirements-txt --no-dev --no-emit-project --output-file requirements.txt
```
```sh
# build the image
docker build -t myimage .
# run the container
docker run -d --name mycontainer -p 80:80 myimage
```
```sh
uv lock --check
uv run ruff check .
uv run ruff format --check .
uv run pytest
```
