require "bundler/gem_tasks"

namespace :sprack do
  desc "build the gem"
  task :gem do
   puts "OHAI"
   exec 'sbt package'
   exec 'gem build sprack.gemspec'
  end
end
