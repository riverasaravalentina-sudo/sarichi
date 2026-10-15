const { defineConfig, devices } = require('@playwright/test');

module.exports = defineConfig({
  testDir: './tests',
  timeout: 90_000,
  expect: { timeout: 15_000 },
  retries: 1,
  workers: 2,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    baseURL: 'https://sarichi-crocheting.onrender.com',
    locale: 'es-CO',
    timezoneId: 'America/Bogota',
    navigationTimeout: 60_000,
    actionTimeout: 15_000,
    screenshot: 'only-on-failure',
    permissions: ['clipboard-read', 'clipboard-write'],
  },
  projects: [
    {
      name: 'desktop',
      use: { ...devices['Desktop Chrome'], viewport: { width: 1280, height: 800 } },
    },
    {
      name: 'tablet',
      use: { ...devices['Desktop Chrome'], viewport: { width: 834, height: 1112 }, hasTouch: true },
    },
    {
      name: 'mobile',
      use: { ...devices['Pixel 5'] },
    },
  ],
});
