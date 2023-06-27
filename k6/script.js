import http from 'k6/http';
import {sleep} from 'k6';

const hostname = `http://${__ENV.DOMAIN}`;
const path = `${__ENV.PATH}`

export default function () {
    http.get(hostname + path);
    sleep(1);
}