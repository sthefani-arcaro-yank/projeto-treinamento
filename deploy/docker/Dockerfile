FROM openjdk:24-jdk-slim



# Instala o chorme
RUN apt-get update && apt-get install -y wget unzip curl dpkg gnupg
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list' && \
    apt-get update &&  \
    apt-get install -y google-chrome-stable &&  \
    rm -rf /var/lib/apt/lists/*

# Instala o xvfb Obs: Utilizado caso precise emular uma tela
#RUN apt-get update
#RUN apt-get install -y xvfb
#CMD (service xvfb start; export DISPLAY=:10; java -jar /app/treinamento-robo/jar/treinamento-robo.jar)

CMD ["java","-jar","/app/treinamento-robo/jar/treinamento-robo.jar"]
