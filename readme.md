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

bundle exec java -jar path/to/sprack.jar --help

0.0.0.1
  -h, --host  <arg>        (default = 0.0.0.0)
  -p, --port  <arg>        (default = 8080)
  -r, --rackfile  <arg>    (default = ./config.ru)
  -t, --timeout  <arg>     (default = 30)
      --help              Show help message
      --version           Show version of this program

```


