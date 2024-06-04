FROM gitpod/workspace-full

USER gitpod

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && \
    sdk install java 21.0.3-tem && \
    sdk default java 21.0.3-tem"

RUN bash -c "brew install go-task/tap/go-task"