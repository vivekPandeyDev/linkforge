import http from 'k6/http';
import { check } from 'k6';

export const options = {
  scenarios: {
    redirect_load: {
      executor: 'constant-arrival-rate',
      rate: 100000,      // 1M requests
      timeUnit: '1s',
      duration: '5s',
      preAllocatedVUs: 20000,
      maxVUs: 50000
    }
  }
};

export default function redirectLoadTest() {
  const shortCode = 'cbbOYEIo'; // cached key
  const res = http.get(`http://localhost:3000/api/v1/${shortCode}`, {
    redirects: 0
  });

  check(res, {
    'status is 302': r => r.status === 302
  });
}