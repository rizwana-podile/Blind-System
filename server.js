const http = require('http');
const fs = require('fs');
const path = require('path');

const DEFAULT_PORT = parseInt(process.env.PORT || '3000', 10);
const PUBLIC_DIR = path.join(__dirname, 'web');

const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.webp': 'image/webp',
  '.ico': 'image/x-icon',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2',
  '.ttf': 'font/ttf'
};

function createServer() {
  return http.createServer((req, res) => {
    // Enable basic CORS
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, HEAD, OPTIONS');

    if (req.method === 'OPTIONS') {
      res.writeHead(204);
      res.end();
      return;
    }

    if (req.method !== 'GET' && req.method !== 'HEAD') {
      res.writeHead(405, { 'Content-Type': 'text/plain; charset=utf-8' });
      res.end('Method Not Allowed');
      return;
    }

    const parsedUrl = new URL(req.url, 'http://localhost');
    let pathname = decodeURIComponent(parsedUrl.pathname);
    if (pathname === '/') pathname = '/index.html';

    const safePath = path.normalize(pathname).replace(/^(\.\.[\/\\])+/, '');
    let targetPath = path.join(PUBLIC_DIR, safePath);

    fs.stat(targetPath, (err, stats) => {
      if (err) {
        res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
        res.end('404 Not Found');
        return;
      }

      if (stats.isDirectory()) {
        targetPath = path.join(targetPath, 'index.html');
      }

      const ext = path.extname(targetPath).toLowerCase();
      const contentType = MIME_TYPES[ext] || 'application/octet-stream';

      fs.readFile(targetPath, (readErr, content) => {
        if (readErr) {
          res.writeHead(500, { 'Content-Type': 'text/plain; charset=utf-8' });
          res.end('500 Internal Server Error');
          return;
        }

        res.writeHead(200, {
          'Content-Type': contentType,
          'Content-Length': Buffer.byteLength(content),
          'Cache-Control': 'no-cache'
        });

        if (req.method === 'HEAD') {
          res.end();
        } else {
          res.end(content);
        }
      });
    });
  });
}

function start(port = DEFAULT_PORT) {
  const server = createServer();

  server.on('error', (err) => {
    if (err.code === 'EADDRINUSE') {
      console.log(`Port ${port} is in use, trying port ${port + 1}...`);
      start(port + 1);
    } else {
      console.error('Server error:', err);
    }
  });

  server.listen(port, '0.0.0.0', () => {
    console.log(`\n==================================================`);
    console.log(`  🦯 SIGHTGUIDE Web Application is running!`);
    console.log(`  Local URL:    http://localhost:${port}`);
    console.log(`  Network URL:  http://127.0.0.1:${port}`);
    console.log(`==================================================\n`);
  });
}

start();
