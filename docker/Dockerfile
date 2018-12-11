FROM fedora:29 AS builder
# docker build -t jeci/saod -f docker/Dockerfile .   
# docker run -it --rm --name saod jeci/saod bash

LABEL name="JECI Stats Alfresco On Database" \
      vendor="Jeci" \
      license="Apache License, Version 2.0" \
      url="https://github.com/jeci-sarl/stats-alfresco-on-database" \
      release="${saod_version}" \
			build-date="${build_date}"

RUN dnf install -y \
			gradle \
	&& dnf clean all

ADD . /opt/saod/
WORKDIR /opt/saod/

# Pre-Build
RUN gradle --no-daemon --no-parallel --no-scan --console plain build

# CMD ["gradle", "--no-daemon", "--no-parallel", "--no-scan", "--console", "plain", "bootRun"]

FROM fedora:29

RUN dnf install -y \
      java-openjdk-headless \
  && dnf clean all

WORKDIR /opt/saod/
COPY --from=builder /opt/saod/build/libs/ .
COPY --from=builder /opt/saod/src/test/resources/application-mysql.properties .

CMD ["java", "-jar", "jeci-saod-0.5.2.jar"]

EXPOSE 8787
