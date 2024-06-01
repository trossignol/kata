default: run

build:
	@rm -rf dist
	@npm run build
	@rm -rf ../server/src/main/resources/META-INF/resources
	@cp -r dist ../server/src/main/resources/META-INF/resources

run:
	@npm run dev