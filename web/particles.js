const canvas = document.getElementById("particles");
canvas.width = innerWidth;
canvas.height = innerHeight;

const c = canvas.getContext("2d");

const amountOfParticles = 1000;
let velocityX = 3;
let velocityY = 2;

let mouseX = 0;
let mouseY = 0;

canvas.onmousemove = function (e) {
    mouseX = e.offsetX;
    mouseY = e.offsetY;
}

window.onresize = function () {
    canvas.width = innerWidth;
    canvas.height = innerHeight;
}

class Particle {

    constructor(x, y, radius, velocityFactorX, velocityFactorY, opacity) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.velocityFactorX = velocityFactorX;
        this.velocityFactorY = velocityFactorY;
        this.opacity = opacity;
    }

    move(cursorDist) {
        const speed = 20/Math.sqrt(cursorDist);

        this.x += velocityX * this.velocityFactorX * speed;
        this.y += velocityY * this.velocityFactorY * speed;

        if(velocityX > 0) {
            if(this.x + this.radius > canvas.width) this.x = -this.radius;
        } else if(velocityX < 0) {
            if(this.x - this.radius < 0) this.x = canvas.width + this.radius;
        }

        if(velocityY > 0) {
            if(this.y + this.radius > canvas.height) this.y = -this.radius;
        } else if(velocityY < 0) {
            if(this.y - this.radius < 0) this.y = canvas.height + this.radius;
        }
    }
}

const particles = [];

for(let i=0; i<amountOfParticles; i++) {
    const x = Math.random() * canvas.width;
    const y = Math.random() * canvas.height;
    const radius = Math.random() * 4 + 1;
    const velocityFactorX = Math.random()+0.5;
    const velocityFactorY = Math.random()+0.5;
    particles.push(new Particle(x, y, radius, velocityFactorX, velocityFactorY, Math.random()/2+0.15));
}

const endAngle = Math.PI * 2;

function dist(x1, y1, x2, y2) {
    const dx = x1-x2;
    const dy = y1-y2;
    return Math.sqrt(dx*dx + dy*dy);
}

setInterval(function () {
    c.clearRect(0, 0, canvas.width, canvas.height);
    particles.forEach(particle => {
        c.fillStyle = "rgba(0, 255, 46, " + particle.opacity + ")";
        c.beginPath();
        c.arc(particle.x, particle.y, particle.radius, 0, endAngle);
        c.closePath();
        c.fill();
        particle.move(dist(particle.x, particle.y, mouseX, mouseY));
    });
}, 25);