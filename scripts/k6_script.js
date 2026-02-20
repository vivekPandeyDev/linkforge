import http from 'k6/http';
import { check } from 'k6';

/* ===============================
   ENV CONFIGURATION
================================ */

const TEST_TYPE   = __ENV.TEST_TYPE || 'capacity';
const BASE_URL    = __ENV.BASE_URL || 'http://localhost:3000';
const SHORT_CODE  = __ENV.SHORT_CODE || 'k4yFF4rIs';
const MISS_RATIO  = Number(__ENV.MISS_RATIO || 15); // every 15th = miss

/* ===============================
   SCENARIO SWITCHER
================================ */

function getScenario() {

    switch (TEST_TYPE) {

        case 'warmup':
            return {
                executor: 'constant-arrival-rate',
                rate: 100,
                timeUnit: '1s',
                duration: '2m',
                preAllocatedVUs: 50,
                maxVUs: 100,
            };

        case 'constant':
            return {
                executor: 'constant-arrival-rate',
                rate: 500,
                timeUnit: '1s',
                duration: '5m',
                preAllocatedVUs: 100,
                maxVUs: 300,
            };

        case 'concurrency':
            return {
                executor: 'ramping-vus',
                stages: [
                    { duration: '1m', target: 100 },
                    { duration: '2m', target: 300 },
                    { duration: '2m', target: 600 },
                    { duration: '1m', target: 0 },
                ],
            };

        case 'capacity':
        default:
            return {
                executor: 'ramping-arrival-rate',
                startRate: 200,
                timeUnit: '1s',
                preAllocatedVUs: 200,
                maxVUs: 1000,
                stages: [
                    { target: 500, duration: '1m' },
                    { target: 1000, duration: '2m' },
                    { target: 1500, duration: '2m' },
                    { target: 2000, duration: '2m' },
                    { target: 0, duration: '1m' },
                ],
            };
    }
}

export const options = {
    scenarios: {
        main_test: getScenario(),
    },
};


/* ===============================
   HELPERS
================================ */

function randomString(length) {
    const chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
}

/* ===============================
   TEST LOGIC
================================ */

export default function () {

    let shortCode;

    // Cache miss logic
    if ((__ITER + 1) % MISS_RATIO === 0) {
        shortCode = randomString(6);
    } else {
        shortCode = SHORT_CODE;
    }

    const res = http.get(`${BASE_URL}/api/v1/${shortCode}`, {
        redirects: 0,
    });

    check(res, {
        'status is 302': (r) => r.status === 302,
    });
}