# sprack: spray rack handler for jruby apps


## develop

```

rbenv local jruby-1.7.3
gem install bundler
bundle install
bundle exec sbt
> test

```

## build

```

bundle exec sbt assembly
...
...
[info] Packaging /github.com/sclasen/sprack/target/scala-2.10/sprack.jar ...
[info] Done packaging.

```

## use

```

bundle exec java -jar path/to/sprack.jar path/to/config.ru

```


