import os
import httpx

USER_SERVICE_HOST_URL = 'http://localhost:9002/api/v2/user/'
url = os.environ.get('USER_SERVICE_HOST_URL') or USER_SERVICE_HOST_URL


def is_user_present(user_id: int):
    r = httpx.get(f'{url}{user_id}')
    return True if r.status_code == 200 else False
